package com.wallet.api.auth;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.api.auth.dto.LoginDto;
import com.wallet.api.auth.dto.LoginResponseDto;
import com.wallet.api.auth.dto.RefreshResponseDto;
import com.wallet.api.auth.dto.RefreshTokenRequestDto;
import com.wallet.api.auth.dto.SignupDto;
import com.wallet.base.exception.BusinessException;
import com.wallet.base.exception.ErrorCode;
import com.wallet.base.security.JwtTokenProvider;
import com.wallet.jpa.auth.RefreshTokenEntity;
import com.wallet.jpa.auth.RefreshTokenRepository;
import com.wallet.jpa.user.UserEntity;
import com.wallet.jpa.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String signup(SignupDto request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String role = (request.getRole() == null || request.getRole().isBlank())
                ? "USER"
                : request.getRole().trim();

        UserEntity user = UserEntity.builder()
                .email(email)
                .password(encodedPassword)
                .name(request.getName().trim())
                .role(role)
                .status("ACTIVE")
                .build();

        userRepository.save(user);
        return "회원가입이 완료되었습니다.";
    }

    @Transactional
    public LoginResponseDto login(LoginDto request) {
        String email = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getRole());

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
                .findByUserIdAndClientTypeAndDeviceId(user.getId(), request.getClientType(), request.getDeviceId())
                .map(existing -> {
                    existing.setToken(refreshToken);
                    existing.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationSeconds()));
                    return existing;
                })
                .orElseGet(() -> RefreshTokenEntity.builder()
                        .userId(user.getId())
                        .clientType(request.getClientType())
                        .deviceId(request.getDeviceId())
                        .token(refreshToken)
                        .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationSeconds()))
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenExpirationSeconds())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequestDto request) {
        jwtTokenProvider.validateToken(request.getRefreshToken());

        String userId = jwtTokenProvider.getSubject(request.getRefreshToken());
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
                .findByUserIdAndClientTypeAndDeviceId(Long.parseLong(userId), request.getClientType(), request.getDeviceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (!refreshTokenEntity.getToken().equals(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        refreshTokenRepository.delete(refreshTokenEntity);
    }

    @Transactional
    public RefreshResponseDto refresh(RefreshTokenRequestDto request) {
        jwtTokenProvider.validateToken(request.getRefreshToken());

        String userId = jwtTokenProvider.getSubject(request.getRefreshToken());
        UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
                .findByUserIdAndClientTypeAndDeviceId(user.getId(), request.getClientType(), request.getDeviceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (!refreshTokenEntity.getToken().equals(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
        return RefreshResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .tokenType("Bearer")
                .build();
    }
}
