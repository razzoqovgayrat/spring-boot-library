package com.library.scheduler;

import com.library.entity.Fine;
import com.library.entity.Loan;
import com.library.enums.FineStatus;
import com.library.enums.LoanStatus;
import com.library.repository.FineRepository;
import com.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Har kuni: ACTIVE va dueDate o'tgan Loan'larni OVERDUE qiladi,
 * va har bir kechikkan kun uchun jarima hisoblab Fine yozuvini yangilaydi.
 *
 * Ledger printsipi: Loan hech qachon o'chirilmaydi/qayta yaratilmaydi,
 * faqat status va Fine.amount yangilanadi.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueLoanScanner {

    private final LoanRepository loanRepository;
    private final FineRepository fineRepository;

    @Value("${library.fine.per-day-amount}")
    private BigDecimal finePerDay;

    @Scheduled(cron = "${scheduler.overdue-scan-cron}")
    @SchedulerLock(
            name = "overdueLoanScanner",
            lockAtMostFor = "PT10M",   // maksimal shuncha vaqt lock saqlanadi, hatto job qulasa ham
            lockAtLeastFor = "PT1M"    // tez tugasa ham, kamida shuncha vaqt boshqa instance urinmasin
    )
    @Transactional
    public void scanOverdueLoans() {
        Instant now = Instant.now();
        List<Loan> overdueLoans = loanRepository.findAllByStatusAndDueDateBefore(LoanStatus.ACTIVE, now);

        log.info("Overdue skaner ishga tushdi: {} ta muddati o'tgan ijara topildi", overdueLoans.size());

        for (Loan loan : overdueLoans) {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
            upsertFine(loan, now);
        }
    }

    private void upsertFine(Loan loan, Instant now) {
        long overdueDays = Math.max(Duration.between(loan.getDueDate(), now).toDays(), 1);
        BigDecimal amount = finePerDay.multiply(BigDecimal.valueOf(overdueDays));

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
}