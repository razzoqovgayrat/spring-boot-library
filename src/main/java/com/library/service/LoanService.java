package com.library.service;

import com.library.dto.request.BorrowRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.LoanResponse;
import com.library.entity.*;
import com.library.enums.*;
import com.library.exception.BusinessRuleViolationException;
import com.library.exception.ConflictException;
import com.library.exception.MemberNotFoundException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookCopyRepository bookCopyRepository;
    private final FineRepository fineRepository;
    private final ReservationRepository reservationRepository;

    @Value("${library.loan.duration-days}")
    private long loanDurationDays;

    @Value("${library.loan.max-active-loans}")
    private int maxActiveLoans;

    @Value("${library.fine.limit-amount}")
    private BigDecimal fineLimitAmount;

    @Value("${library.fine.per-day-amount}")
    private BigDecimal finePerDayAmount;

    @Value("${library.reservation.ready-window-hours}")
    private long readyWindowHours;

    private static final List<LoanStatus> ACTIVE_LOAN_STATUSES = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    @Transactional
    public ApiResponse<CreatedResponse> borrow(BorrowRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new MemberNotFoundException("member not found"));
        validateMemberCanBorrow(member);

        BookCopy bookCopy = bookCopyRepository.findById(request.copyId())
                .orElseThrow(() -> new ResourceNotFoundException("copy not found"));

        if (bookCopy.getStatus().equals(BookCopyStatus.BORROWED)
                || bookCopy.getStatus().equals(BookCopyStatus.LOST))
            throw new BusinessRuleViolationException("COPY_NOT_AVAILABLE",
                    "Nusxa hozir mavjud emas: " + bookCopy.getInventoryNumber());

        Reservation fulfilledReservation = resolveCopyAvailability(bookCopy, member);

        Instant now = Instant.now();
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        Loan loan = Loan.builder()
                .borrowedAt(now)
                .member(member)
                .copy(bookCopy)
                .dueDate(now.plus(Duration.ofDays(loanDurationDays)))
                .build();
        try {
            // @Version orqali optimistic lock — ikki kishi bir vaqtda shu
            // nusxani olishga urinsa, biri shu yerda xato oladi.
            bookCopyRepository.save(bookCopy);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessRuleViolationException("COPY_NOT_AVAILABLE",
                    "Nusxa aynan shu paytda boshqa kimdir tomonidan olindi, qayta urinib ko'ring");
        }
        loanRepository.save(loan);

        if (Objects.nonNull(fulfilledReservation)) {
            fulfilledReservation.setStatus(ReservationStatus.FULFILLED);
            reservationRepository.save(fulfilledReservation);
        }

        return ApiResponse.success("Kitob ijaraga berildi", new CreatedResponse(loan.getId()));
    }

    private Reservation resolveCopyAvailability(BookCopy copy, Member member) {
        if (copy.getStatus().equals(BookCopyStatus.AVAILABLE)) return null;

        Reservation reservation = reservationRepository
                .findFirstByBookIdAndMemberIdAndStatus(copy.getBook().getId(), member.getId(), ReservationStatus.READY)
                .orElseThrow(() -> new BusinessRuleViolationException("COPY_NOT_AVAILABLE",
                        "Bu nusxa boshqa a'zo uchun band qilingan"));

        if (Objects.nonNull(reservation.getReadyUntil()) && reservation.getReadyUntil().isBefore(Instant.now())) {
            throw new BusinessRuleViolationException("RESERVATION_EXPIRED",
                    "Navbat muddati tugagan, qaytadan navbatga yoziling");
        }

        return reservation;
    }


    @Transactional
    public ApiResponse<LoanResponse> markAsLost(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan topilmadi: " + loanId));

        if (loan.getStatus().equals(LoanStatus.RETURNED) || loan.getStatus().equals(LoanStatus.LOST)) {
            throw new BusinessRuleViolationException("LOAN_ALREADY_CLOSED",
                    "Bu ijara allaqachon " + loan.getStatus() + " holatida");
        }

        loan.setStatus(LoanStatus.LOST);
        loanRepository.save(loan);

        BookCopy copy = loan.getCopy();
        copy.setStatus(BookCopyStatus.LOST);
        bookCopyRepository.save(copy);

        // Diqqat: yo'qolgan kitob uchun jarima summasi (masalan kitob narxi)
        // talabnomada berilmagan — hozircha kunlik kechikish jarimasidan
        // farqli alohida qoida yo'q. Kerak bo'lsa shu yerga qo'shiladi.

        LoanResponse loanResponse = LoanResponse.fromEntity(loan);
        return ApiResponse.success("Yo'qolgan deb belgilandi", loanResponse);
    }

    @Transactional
    public ApiResponse<Void> returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("loan not found"));

        if (loan.getStatus().equals(LoanStatus.RETURNED) || loan.getStatus().equals(LoanStatus.LOST)) {
            throw new ConflictException("loan already returned or lost");
        }

        Instant now = Instant.now();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(now);
        loanRepository.save(loan);

        applyLateFineIfNeeded(loan, now);
        releaseOrReserveCopy(loan.getCopy(), now);

        return ApiResponse.success("Kitob qaytarildi");
    }


    private void applyLateFineIfNeeded(Loan loan, Instant now) {
        if (now.isBefore(loan.getDueDate())) return;

        long overdueDays = Math.max(Duration.between(loan.getDueDate(), now).toDays(), 1);
        BigDecimal amount = finePerDayAmount.multiply(BigDecimal.valueOf(overdueDays));

        Fine fine = fineRepository.findByLoanIdAndStatus(loan.getId(), FineStatus.UNPAID)
                .orElseGet(() -> Fine.builder()
                        .loan(loan)
                        .member(loan.getMember())
                        .status(FineStatus.UNPAID)
                        .reason("Kitobni muddatida qaytarmagani uchun jarima")
                        .build());

        fine.setAmount(amount);
        fineRepository.save(fine);
    }

    private void releaseOrReserveCopy(BookCopy copy, Instant now) {
        var nextInQueue = reservationRepository
                .findFirstByBookIdAndStatusOrderByCreatedAtAsc(copy.getBook().getId(), ReservationStatus.WAITING);

        if (nextInQueue.isPresent()) {
            Reservation reservation = nextInQueue.get();
            reservation.setStatus(ReservationStatus.READY);
            reservation.setReadyUntil(now.plus(readyWindowHours, ChronoUnit.HOURS));
            reservationRepository.save(reservation);
            copy.setStatus(BookCopyStatus.RESERVED);
        } else {
            copy.setStatus(BookCopyStatus.AVAILABLE);
        }
        bookCopyRepository.save(copy);
    }

    @Transactional(readOnly = true)
    public ApiResponse<LoanResponse> getById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("loan not found"));

        return ApiResponse.success(LoanResponse.fromEntity(loan));
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<LoanResponse>> getAll(Long memberId, String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return null;
        }
        LoanStatus loanStatus;
        try {
            loanStatus = LoanStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Noto'g'ri status qiymati: " + status
                    + ". Mumkin bo'lgan qiymatlar: ACTIVE, OVERDUE, RETURNED, LOST");
        }

        Page<LoanResponse> loans = loanRepository
                .search(memberId, loanStatus, pageable)
                .map(LoanResponse::fromEntity);

        return ApiResponse.success(loans);
    }

    private void validateMemberCanBorrow(Member member) {
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) {
            throw new BusinessRuleViolationException("MEMBER_SUSPENDED",
                    "A'zo hozir SUSPENDED holatida, ijaraga olib bo'lmaydi");
        }

        long activeLoans = loanRepository.countByMemberIdAndStatusIn(member.getId(), ACTIVE_LOAN_STATUSES);
        if (activeLoans >= maxActiveLoans) {
            throw new BusinessRuleViolationException("MEMBER_LOAN_LIMIT_EXCEEDED",
                    "A'zoda maksimal " + maxActiveLoans + " ta aktiv ijara bor, yangisini olib bo'lmaydi");
        }

        BigDecimal unpaidTotal = fineRepository.sumUnpaidAmountByMemberId(member.getId());
        if (unpaidTotal.compareTo(fineLimitAmount) >= 0) {
            throw new BusinessRuleViolationException("MEMBER_FINE_LIMIT_EXCEEDED",
                    "A'zoning to'lanmagan jarimasi limitdan oshgan (" + unpaidTotal + "), yangi ijara berilmaydi");
        }
    }
}
