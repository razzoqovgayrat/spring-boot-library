package com.library.service;

import com.library.dto.request.LoginRequest;
import com.library.dto.request.LogoutRequest;
import com.library.dto.request.RefreshTokenRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.RefreshTokenResponse;
import com.library.dto.response.SessionResponse;
import com.library.dto.response.UserResponse;
import com.library.entity.DeviceInfo;
import com.library.entity.Session;
import com.library.entity.User;
import com.library.enums.UserStatus;
import com.library.exception.InvalidRefreshTokenException;
import com.library.repository.SessionRepository;
import com.library.repository.UserRepository;
import com.library.util.OpaqueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.refreshTokenLiveTime}")
    private int refreshTokenLiveTime;

    @Transactional
    public ApiResponse<UserResponse> login(LoginRequest loginRequest, String deviceName) {

        String username = loginRequest.getUsername();
        String deviceId = loginRequest.getDeviceId();

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword())
        );

        if (!authenticate.isAuthenticated()) {
            throw new UsernameNotFoundException("username yoki parol xato");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.encode(user);
        String refreshToken = OpaqueUtil.generateToken();
        String encodedRefreshToken = passwordEncoder.encode(refreshToken);

        Session session = sessionRepository
                .findByUserUsernameAndDeviceInfoDeviceIdAndRevokedAtIsNull(username, deviceId)
                .orElseGet(() -> Session.builder().user(user).build());

        session.setDeviceInfo(new DeviceInfo(deviceId, deviceName));
        session.setRefreshToken(encodedRefreshToken);
        session.setLastUsedAt(LocalDateTime.now());
        user.setLastLoginAt(Instant.now());
        sessionRepository.save(session);

        return ApiResponse.success(UserResponse.fromLogin(user, accessToken, refreshToken));
    }

    /**
     * Refresh token orqali yangi access token olish.
     * Refresh token bazada hash holida saqlangani uchun to'g'ridan-to'g'ri qidirilmaydi —
     * shu userning shu deviceId bo'yicha aktiv sessiyasi topilib, keyin encode taqqoslanadi.
     */
    @Transactional
    public ApiResponse<RefreshTokenResponse> refresh(RefreshTokenRequest request) {
        Session session = sessionRepository
                .findByDeviceInfoDeviceIdAndRevokedAtIsNull(request.deviceId())
                .orElseThrow(() -> new InvalidRefreshTokenException("Session not found or revoked"));

        if (!passwordEncoder.matches(request.refreshToken(), session.getRefreshToken())) {
            throw new InvalidRefreshTokenException("Refresh token is wrong");
        }

        if (session.getLastUsedAt().plusDays(refreshTokenLiveTime).isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Session has expired please login");
        }

        User user = session.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidRefreshTokenException("User is not ACTIVE");
        }


        String newAccessToken = jwtService.encode(user);
        String newRefreshToken = OpaqueUtil.generateToken();

        session.setRefreshToken(passwordEncoder.encode(newRefreshToken));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);

        SessionResponse sessionResponse = new SessionResponse(
                session.getDeviceInfo().getDeviceName(),
                session.getLastUsedAt()
        );

        return ApiResponse.success(new RefreshTokenResponse(newAccessToken, newRefreshToken, sessionResponse));
    }

    /**
     * Faqat joriy qurilmadagi sessiyani bekor qiladi ("logout from this device").
     */
    @Transactional
    public ApiResponse<Void> logout(String username, LogoutRequest request) {
        Session session = sessionRepository
                .findByUserUsernameAndDeviceInfoDeviceIdAndRevokedAtIsNull(username, request.deviceId())
                .orElseThrow(() -> new InvalidRefreshTokenException("Session not found"));

        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return ApiResponse.success("successfully logout");
    }

    /**
     * Barcha qurilmalardan chiqarish ("logout everywhere") — masalan
     * "parolim o'g'irlanganga o'xshaydi" holatida ishlatiladi.
     */
    @Transactional
    public ApiResponse<Void> logoutAll(String username) {
        sessionRepository.revokeAllActiveSessionsByUsername(username, LocalDateTime.now());
        return ApiResponse.success("successfully logout");
    }
}