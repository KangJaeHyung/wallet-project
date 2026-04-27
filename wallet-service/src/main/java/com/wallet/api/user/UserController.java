package com.wallet.api.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.api.user.dto.UserResponseDto;
import com.wallet.api.user.dto.UserUpdateDto;
import com.wallet.base.ResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseDto<UserResponseDto> getUser(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("userId: {}", userId);

        UserResponseDto userResponse = userService.getUser(userId);
        return ResponseDto.success("사용자 정보를 조회했습니다.", 200, userResponse);
    }

    @PostMapping("/me")
    public ResponseDto<Void> updateUser(Authentication authentication, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("userId: {}", userId);
        return ResponseDto.success(userService.updateUser(userId, userUpdateDto), 200, null);
    }
}
