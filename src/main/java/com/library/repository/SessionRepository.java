package com.library.repository;

import com.library.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByUserUsernameAndDeviceInfoDeviceId(String userUsername, String deviceInfoDeviceId);

    Optional<Session> findByRefreshTokenAndDeviceInfoDeviceId(String refreshToken, String deviceInfoDeviceId);

    Optional<Session> getByDeviceInfoDeviceId(String deviceInfoDeviceId);
}
