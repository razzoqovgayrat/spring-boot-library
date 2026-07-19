package com.library.repository;

import com.library.entity.Fine;
import com.library.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {
    // shu Loan uchun jarima allaqachon ochilganmi (UNPAID) — bo'lsa yangilanadi,
    // bo'lmasa yangi Fine yaratiladi (overdue job har kuni qayta ishlaganda
    // bitta Loan uchun bir nechta Fine hosil bo'lib ketmasligi uchun)
    Optional<Fine> findByLoanIdAndStatus(Long loanId, FineStatus status);

    Optional<Fine> findByIdAndStatus(Long id, FineStatus status);


    // MEMBER_FINE_LIMIT_EXCEEDED tekshiruvi uchun
    @Query("""
            select coalesce(sum(f.amount), 0) from Fine f
            where f.member.id = :memberId and f.status = FineStatus.UNPAID
            """)
    BigDecimal sumUnpaidAmountByMemberId(@Param("memberId") Long memberId);

    // memberId (Long) va status (enum) — ikkalasi ham ixtiyoriy, is null xavfsiz
    // (Loan'dagi kabi — text/lower() funksiyasi ishlatilmagani uchun bytea xatosi yo'q)
    @Query("""
            select f from Fine f
            where (:memberId is null or f.member.id = :memberId)
              and (:status is null or f.status = :status)
            """)
    Page<Fine> search(
            @Param("memberId") Long memberId,
            @Param("status") FineStatus status,
            Pageable pageable
    );
}
