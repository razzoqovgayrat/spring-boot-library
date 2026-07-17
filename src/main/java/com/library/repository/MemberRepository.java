package com.library.repository;

import com.library.entity.Member;
import com.library.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("""
            select m from Member m
            where (:fullName is null or lower(m.fullName) like lower(concat('%', cast(:fullName as string), '%')))
              and (:status is null or m.status = :status) and m.deletedAt is null
            """)
    Page<Member> search(
            @Param("fullName") String fullName,
            @Param("status") MemberStatus status,
            Pageable pageable
    );

    boolean existsByMembershipNumberAndDeletedAtIsNull(String membershipNumber);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByMembershipNumber(String membershipNumber);

    Optional<Member> findByIdAndDeletedAtIsNullAndStatus(Long id, MemberStatus status);
}
