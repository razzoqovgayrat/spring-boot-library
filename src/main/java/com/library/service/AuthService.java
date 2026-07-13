package com.library.service;

import com.library.dto.request.LogOutRequest;
import com.library.dto.request.LoginRequest;
import com.library.dto.request.RefreshTokenRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.response.SessionResponse;
import com.library.dto.response.TokenResponse;
import com.library.dto.response.UserResponse;
import com.library.entity.DeviceInfo;
import com.library.entity.Session;
import com.library.entity.User;
import com.library.enums.UserStatus;
import com.library.exception.AppBadRequestException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.SessionRepository;
import com.library.repository.UserRepository;
import com.library.util.HashUtil;
import com.library.util.OpaqueUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;
    @Value("${app.refreshTokenLiveTime}")
    private int refreshTokenLiveTime;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .visible(true)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse authorization(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                String deviceName = httpServletRequest.getHeader("User-Agent");
                User userDetails = (User) authenticate.getPrincipal();
                String accessToken = jwtService.encode(loginRequest.getUsername(), userDetails.getRole().name());
                String refreshToken = OpaqueUtil.generateToken();
                String hashedRefreshToken = HashUtil.sha256(refreshToken);
                Optional<Session> optional = sessionRepository.findByUserUsernameAndDeviceInfoDeviceId(loginRequest.getUsername(), loginRequest.getDeviceId());
                Session session;
                if (optional.isPresent()) {
                    session = optional.get();
                } else {
                    DeviceInfo devInfo = new DeviceInfo();
                    devInfo.setDeviceName(deviceName);
                    devInfo.setDeviceId(loginRequest.getDeviceId());

                    session = new Session();
                    session.setUser(userDetails);
                    session.setDeviceInfo(devInfo);
                }
                session.setRefreshToken(hashedRefreshToken);
                session.setLastUsedAt(LocalDateTime.now());
                sessionRepository.save(session);

                SessionResponse sessionResponse = new SessionResponse(session.getDeviceInfo().getDeviceName(), session.getLastUsedAt());
                return UserResponse.builder()
                        .fullName(userDetails.getFullName())
                        .username(userDetails.getUsername())
                        .role(userDetails.getRole())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .sessionResponse(sessionResponse)
                        .build();
            }
        } catch (BadCredentialsException ignored) {
        }
        throw new UsernameNotFoundException("phone or password is wrong");
    }

    public TokenResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest, HttpServletRequest httpServletRequest) {
        String hashed = HashUtil.sha256(refreshTokenRequest.refreshToken());
        Optional<Session> optional = sessionRepository.findByRefreshTokenAndDeviceInfoDeviceId(hashed, refreshTokenRequest.deviceId());
        if (optional.isEmpty()) throw new ResourceNotFoundException("Session not found");
        Session session = optional.get();
        User user = session.getUser();
        if (user.getStatus().equals(UserStatus.BLOCKED)) {
            throw new AppBadRequestException("User Blocked");
        }
        if (session.getLastUsedAt().plusDays(refreshTokenLiveTime).isAfter(LocalDateTime.now())) {
            String accessToken = jwtService.encode(user.getUsername(), user.getRole().name());
            String refreshToken = OpaqueUtil.generateToken();
            String hashedRefreshToken = HashUtil.sha256(refreshToken);
            session.setRefreshToken(hashedRefreshToken);
            session.setLastUsedAt(LocalDateTime.now());
            sessionRepository.save(session);
            return new TokenResponse(accessToken, refreshToken);
        }
        throw new AppBadRequestException("Refresh token expired");
    }

    public void logout(LogOutRequest logOutRequest) {
        String deviceId = logOutRequest.deviceId();
        Optional<Session> optional = sessionRepository.getByDeviceInfoDeviceId(deviceId);
        if (optional.isEmpty()) throw new ResourceNotFoundException("Session not found");
        Session session = optional.get();
        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }
}
