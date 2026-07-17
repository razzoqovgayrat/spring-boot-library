package com.library.repository;

import com.library.entity.Session;
import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByUserUsernameAndDeviceInfoDeviceIdAndRevokedAtIsNull(
            String username, String deviceId
    );

    Optional<Session> findByDeviceInfoDeviceIdAndRevokedAtIsNull(String deviceId);

    @Query("""
            select s from Session s
            join s.user u
            where u.username = :username
              and s.revokedAt is null
            """)
    List<Session> findAllActiveByUsername(@Param("username") String username, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Session s
            set s.revokedAt = :revokedAt
            where s.user.id in (
                select u.id from User u where u.username = :username
            )
              and s.revokedAt is null
            """)
    void revokeAllActiveSessionsByUsername(@Param("username") String username, @Param("revokedAt") LocalDateTime revokedAt);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Session s
            set s.revokedAt = :now
            where s.revokedAt is null
            """)
    int revokeAllExpiredSessions(@Param("now") LocalDateTime now);

    String user(User user);
}