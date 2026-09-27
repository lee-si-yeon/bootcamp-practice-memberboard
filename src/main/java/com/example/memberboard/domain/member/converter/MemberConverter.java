package com.example.memberboard.domain.member.converter;

import com.example.memberboard.domain.member.dto.MemberRequestDto;
import com.example.memberboard.domain.member.dto.MemberResponseDto;
import com.example.memberboard.domain.member.entity.Member;

public class MemberConverter {

    // DTO -> 회원 엔티티
    public static Member toMember(MemberRequestDto.SignUpDTO request, String encodedPassword) {
        return Member.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .build();
    }

    // entity -> 일반 회원가입 DTO
    public static MemberResponseDto.SignUpResultDTO toSignUpResultDTO(Member member) {
        return MemberResponseDto.SignUpResultDTO.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .build();
    }

    // entity -> 일반 로그인 DTO
    public static MemberResponseDto.LoginResultDTO toLoginResultDTO(Member member, String accessToken) {
        return MemberResponseDto.LoginResultDTO.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .build();
    }
}
