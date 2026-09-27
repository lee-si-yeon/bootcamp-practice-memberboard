package com.example.memberboard.domain.member.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class MemberResponseDto {

    // 일반 회원가입 결과
    @Builder
    public record SignUpResultDTO(
            Long memberId,
            String email,
            String nickname,
            LocalDateTime createdAt
    ) {}
}
