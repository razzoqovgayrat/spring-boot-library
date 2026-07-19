package com.library.scheduler;

import com.library.entity.BookCopy;
import com.library.entity.Reservation;
import com.library.enums.BookCopyStatus;
import com.library.enums.ReservationStatus;
import com.library.repository.BookCopyRepository;
import com.library.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Har kuni: readyUntil o'tgan READY navbatlarni EXPIRED qiladi, va o'sha
 * kitob uchun navbatdagi keyingi WAITING a'zoni topib, unga READY beradi
 * (nusxa RESERVED holatida qoladi — bo'shatilmaydi, chunki endi keyingi
 * kishiga ajratiladi).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryJob {

    private final ReservationRepository reservationRepository;
    private final BookCopyRepository bookCopyRepository;

    @Value("${library.reservation.ready-window-hours}")
    private long readyWindowHours;

    @Scheduled(cron = "${scheduler.reservation-expiry-cron}")
    @SchedulerLock(
            name = "reservationExpiryJob",
            lockAtMostFor = "PT10M",
            lockAtLeastFor = "PT1M"
    )
    @Transactional
    public void expireReadyReservations() {
        Instant now = Instant.now();
        List<Reservation> expired = reservationRepository
                .findAllByStatusAndReadyUntilBefore(ReservationStatus.READY, now);

        log.info("Reservation expiry job ishga tushdi: {} ta muddati o'tgan navbat topildi", expired.size());

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            promoteNextInQueue(reservation, now);
        }
    }

    private void promoteNextInQueue(Reservation expiredReservation, Instant now) {
        Long bookId = expiredReservation.getBook().getId();

        Optional<Reservation> next = reservationRepository
                .findFirstByBookIdAndStatusOrderByCreatedAtAsc(bookId, ReservationStatus.WAITING);

        if (next.isEmpty()) {
            // Navbatda hech kim qolmagan — nusxani haqiqiy AVAILABLE qilib qo'yamiz
            releaseCopyToAvailable(bookId);
            return;
        }

        Reservation nextReservation = next.get();
        nextReservation.setStatus(ReservationStatus.READY);
        nextReservation.setReadyUntil(now.plusSeconds(readyWindowHours * 3600));
        reservationRepository.save(nextReservation);
        // Nusxa RESERVED holatida qoladi — endi navbatdagi keyingi kishiga ajratilgan
    }

    private void releaseCopyToAvailable(Long bookId) {
        // RESERVED holatidagi, shu kitobga tegishli nusxani topib, bo'shatamiz.
        // Diqqat: bir nechta RESERVED nusxa bo'lishi mumkin (bir nechta a'zo
        // bitta kitobning turli nusxalarini band qilgan bo'lishi mumkin emas —
        // odatda faqat bitta nusxa RESERVED bo'ladi, lekin xavfsizlik uchun
        // "birinchisini" olamiz).
        Optional<BookCopy> reservedCopy = bookCopyRepository
                .findFirstByBookIdAndStatus(bookId, BookCopyStatus.RESERVED);

        reservedCopy.ifPresent(copy -> {
            copy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);
        });
    }
}