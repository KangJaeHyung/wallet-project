package com.wallet.api.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.api.auth.dto.LoginDto;
import com.wallet.api.auth.dto.LoginResponseDto;
import com.wallet.api.auth.dto.RefreshResponseDto;
import com.wallet.api.auth.dto.RefreshTokenRequestDto;
import com.wallet.api.auth.dto.SignupDto;
import com.wallet.base.ResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseDto<String> signup(@RequestBody @Valid SignupDto request) {
        String message = authService.signup(request);
        return ResponseDto.success(message, 201, null);
    }

    @PostMapping("/login")
    public ResponseDto<LoginResponseDto> login(@RequestBody @Valid LoginDto request) {
        LoginResponseDto loginResponse = authService.login(request);
        return ResponseDto.success("로그인이 완료되었습니다.", 200, loginResponse);
    }

    @PostMapping("/logout")
    public ResponseDto<Void> logout(@RequestBody @Valid RefreshTokenRequestDto request) {
        authService.logout(request);
        return ResponseDto.success("로그아웃이 완료되었습니다.", 200, null);
    }

    @PostMapping("/refresh")
    public ResponseDto<RefreshResponseDto> refresh(@RequestBody @Valid RefreshTokenRequestDto request) {
        RefreshResponseDto refreshResponse = authService.refresh(request);
        return ResponseDto.success("토큰 갱신이 완료되었습니다.", 200, refreshResponse);
    }
}
