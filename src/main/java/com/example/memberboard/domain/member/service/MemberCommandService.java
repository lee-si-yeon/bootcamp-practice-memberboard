package com.example.memberboard.domain.member.service;

import com.example.memberboard.domain.member.converter.MemberConverter;
import com.example.memberboard.domain.member.dto.MemberRequestDto;
import com.example.memberboard.domain.member.dto.MemberResponseDto;
import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDto.SignUpResultDTO signup(MemberRequestDto.SignUpDTO request) {

        if (memberRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = MemberConverter.toMember(request, encodedPassword);
        Member savedMember = memberRepository.save(member);

        return MemberConverter.toSignUpResultDTO(savedMember);
    }
}
