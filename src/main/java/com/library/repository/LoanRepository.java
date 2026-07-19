package com.library.repository;

import com.library.entity.Loan;
import com.library.entity.Reservation;
import com.library.enums.LoanStatus;
import com.library.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByIdAndDeletedAtIsNull(Long id);

    Optional<Loan> findByCopyId(Long copyId);

    // Diqqat: memberId (Long) va status (enum) uchun "is null" naqshi
    // xavfsiz — Book qidiruvidagi bytea xatosi faqat String + lower()/concat
    // kombinatsiyasida bo'ladi. Long/enum uchun bunday muammo yo'q, chunki
    // ular lower() orqali o'tmaydi, aniq SQL turiga (bigint/varchar) ega.
    @Query("""
            select l from Loan l
            where (:memberId is null or l.member.id = :memberId)
              and (:status is null or l.status = :status) and l.deletedAt is null
            """)
    Page<Loan> search(
            @Param("memberId") Long memberId,
            @Param("status") LoanStatus status,
            Pageable pageable
    );

    List<Loan> findByMemberId(Long memberId);
    List<Loan> findAllByStatusAndDueDateBefore(LoanStatus status, Instant now);

    List<Loan> findByMemberIdAndReturnedAtIsNull(Long memberId);
    long countByMemberIdAndStatusIn(Long memberId, List<LoanStatus> statuses);
}
