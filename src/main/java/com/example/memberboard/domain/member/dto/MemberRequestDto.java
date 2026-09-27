package com.example.memberboard.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequestDto {

    // 일반 회원가입
    public record SignUpDTO(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            @Schema(description = "이메일", example = "test@example.com")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
            @Schema(description = "비밀번호 (8자 이상)", example = "password123")
            String password,

            @NotBlank(message = "닉네임은 필수입니다.")
            @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
            @Schema(description = "닉네임 (20자 이하)", example = "tester")
            String nickname
    ) {}

    // 일반 로그인
    public record LoginDTO(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            @Schema(description = "이메일", example = "test@example.com")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Schema(description = "비밀번호", example = "password123")
            String password
    ) {}
}
