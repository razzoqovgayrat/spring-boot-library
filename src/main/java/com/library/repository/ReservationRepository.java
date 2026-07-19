package com.library.repository;

import com.library.entity.Reservation;
import com.library.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // muddati o'tgan READY navbatlarni topish (expiry job uchun)
    List<Reservation> findAllByStatusAndReadyUntilBefore(ReservationStatus status, Instant now);

    // shu kitob uchun navbatdagi eng birinchi WAITING a'zoni topish (FIFO)
    Optional<Reservation> findFirstByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);

    // a'zoning navbatlari
    Page<Reservation> findByMemberId(Long memberId, Pageable pageable);

    // kitobning navbat ro'yxati
    Page<Reservation> findByBookId(Long bookId, Pageable pageable);

    // navbatdagi kishi o'z kitobini olishga kelganda (borrow) tekshirish uchun
    Optional<Reservation> findFirstByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, ReservationStatus status);

    // shu a'zo shu kitobga allaqachon WAITING/READY navbatda turibdimi
    // (bir xil kitobga ikki marta navbat yozilib qolmasligi uchun)
    boolean existsByBookIdAndMemberIdAndStatusIn(Long bookId, Long memberId, List<ReservationStatus> statuses);

    // WAITING navbatda shu reservation'dan OLDIN turganlar soni -> queuePosition
    long countByBookIdAndStatusAndCreatedAtBefore(Long bookId, ReservationStatus status, Instant createdAt);
}
