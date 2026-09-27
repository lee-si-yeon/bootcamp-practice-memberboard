package com.example.memberboard.domain.member.converter;

import com.example.memberboard.domain.member.dto.MemberRequestDto;
import com.example.memberboard.domain.member.dto.MemberResponseDto;
import com.example.memberboard.domain.member.entity.Member;

public class MemberConverter {

    // 일반 회원가입 요청 DTO -> Member
    public static Member toMember(MemberRequestDto.SignUpDTO request, String encodedPassword) {
        return Member.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .build();
    }

    // Member -> 일반 회원가입 응답 DTO
    public static MemberResponseDto.SignUpResultDTO toSignUpResultDTO(Member member) {
        return MemberResponseDto.SignUpResultDTO.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
