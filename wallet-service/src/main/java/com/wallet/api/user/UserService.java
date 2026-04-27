package com.wallet.api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.api.user.dto.UserResponseDto;
import com.wallet.api.user.dto.UserUpdateDto;
import com.wallet.base.exception.BusinessException;
import com.wallet.base.exception.ErrorCode;
import com.wallet.jpa.user.UserEntity;
import com.wallet.jpa.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNKNOW_USER));

        if (user.getStatus().equals("INACTIVE")) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }
        return UserResponseDto.builder()        
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Transactional
    public String updateUser(Long userId, UserUpdateDto userUpdateDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNKNOW_USER));

        if (user.getStatus().equals("INACTIVE")) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        if (userUpdateDto.getPassword() != null) {
            if (!passwordEncoder.matches(userUpdateDto.getPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.UNMATCHED_PASSWORD);
            }
            if (userUpdateDto.getNewPassword() == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            user.setPassword(passwordEncoder.encode(userUpdateDto.getNewPassword()));
            return "비밀번호가 수정되었습니다.";
        }
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getStatus() != null) {
            user.setStatus(userUpdateDto.getStatus());
        }
        userRepository.saveAndFlush(user);
        return "사용자 정보가 수정되었습니다.";
    }
}
