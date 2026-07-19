package com.library.service;

import com.library.dto.request.ReservationRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.ReservationResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Member;
import com.library.entity.Reservation;
import com.library.enums.BookCopyStatus;
import com.library.enums.MemberStatus;
import com.library.enums.ReservationStatus;
import com.library.exception.BusinessRuleViolationException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.ReservationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${library.reservation.ready-window-hours}")
    private long readyWindowHours;


    private static final List<ReservationStatus> ACTIVE_RESERVATION_STATUSES = List.of(ReservationStatus.WAITING, ReservationStatus.READY);

    public ApiResponse<ReservationResponse> create(@Valid ReservationRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member topilmadi: " + request.memberId()));

        if (!member.getStatus().equals(MemberStatus.ACTIVE)) {
            throw new BusinessRuleViolationException("MEMBER_SUSPENDED", "A'zo SUSPENDED holatida, navbatga yozilolmaydi");
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book topilmadi: " + request.bookId()));

        boolean alreadyQueued = reservationRepository.existsByBookIdAndMemberIdAndStatusIn(book.getId(), member.getId(), ACTIVE_RESERVATION_STATUSES);
        if (alreadyQueued) {
            throw new BusinessRuleViolationException("ALREADY_RESERVED", "A'zo bu kitobga allaqachon navbatda turibdi");
        }

        boolean hasAvailableCopy = bookCopyRepository.findFirstByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).isPresent();
        if (hasAvailableCopy) {
            throw new BusinessRuleViolationException("COPY_AVAILABLE",
                    "Bu kitobning bo'sh nusxasi bor, navbatga yozish shart emas — to'g'ridan-to'g'ri borrow qiling");
        }

        Reservation reservation = Reservation.builder()
                .book(book)
                .member(member)
                .status(ReservationStatus.WAITING)
                .build();
        reservation.setCreatedAt(Instant.now());
        reservation.setCreatedBy(customUserDetailsService.getCurrentUser().getUsername());

        reservationRepository.save(reservation);
        Integer queuePosition = calculateQueuePosition(reservation);

        return ApiResponse.success("Navbatga yozildi", ReservationResponse.fromEntity(reservation, queuePosition));
    }


    private Integer calculateQueuePosition(Reservation reservation) {
        if (!reservation.getStatus().equals(ReservationStatus.WAITING)) return null;
        long before = reservationRepository
                .countByBookIdAndStatusAndCreatedAtBefore(reservation.getBook().getId(), ReservationStatus.WAITING, reservation.getCreatedAt());

        return (int) before + 1;
    }

    public ApiResponse<Void> cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation topilmadi: " + id));

        if (!ACTIVE_RESERVATION_STATUSES.contains(reservation.getStatus())) {
            throw new BusinessRuleViolationException("RESERVATION_NOT_CANCELLABLE",
                    "Faqat WAITING yoki READY holatidagi navbatni bekor qilish mumkin");
        }

        boolean wasReady = reservation.getStatus().equals(ReservationStatus.READY);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Agar bu navbat READY bo'lib, nusxa uning uchun RESERVED holatida
        // turgan bo'lsa — endi shu nusxani navbatdagi keyingisiga o'tkazish
        // yoki hech kim qolmasa AVAILABLE qilish kerak.
        if (wasReady) {
            promoteNextOrRelease(reservation.getBook().getId());
        }

        return ApiResponse.success("Navbat bekor qilindi");
    }

    private void promoteNextOrRelease(Long bookId) {
        Optional<Reservation> next = reservationRepository.findFirstByBookIdAndStatusOrderByCreatedAtAsc(bookId, ReservationStatus.WAITING);

        Optional<BookCopy> reservedCopy = bookCopyRepository.findFirstByBookIdAndStatus(bookId, BookCopyStatus.RESERVED);
        if (reservedCopy.isEmpty()) return;

        if (next.isPresent()) {
            Reservation nextReservation = next.get();
            nextReservation.setStatus(ReservationStatus.READY);
            nextReservation.setReadyUntil(Instant.now().plus(readyWindowHours, ChronoUnit.HOURS));
            reservationRepository.save(nextReservation);
            // nusxa RESERVED holatida qoladi, faqat egasi almashadi
        } else {
            BookCopy copy = reservedCopy.get();
            copy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<ReservationResponse> getById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation topilmadi: " + id));

        return ApiResponse.success(ReservationResponse.fromEntity(reservation, calculateQueuePosition(reservation)));
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<ReservationResponse>> getByMember(Long memberId, Pageable pageable) {
        Page<ReservationResponse> reservationResponses = reservationRepository.findByMemberId(memberId, pageable).map(r -> ReservationResponse.fromEntity(r, calculateQueuePosition(r)));

        return ApiResponse.success(reservationResponses);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<ReservationResponse>> getByBook(Long bookId, Pageable pageable) {
        Page<ReservationResponse> reservationResponses = reservationRepository.findByBookId(bookId, pageable).map(r -> ReservationResponse.fromEntity(r, calculateQueuePosition(r)));

        return ApiResponse.success(reservationResponses);
    }
}
