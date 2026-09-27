package com.example.memberboard.domain.member.service;

import com.example.memberboard.domain.member.converter.MemberConverter;
import com.example.memberboard.domain.member.dto.MemberRequestDto;
import com.example.memberboard.domain.member.dto.MemberResponseDto;
import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.member.repository.MemberRepository;
import com.example.memberboard.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberResponseDto.SignUpResultDTO signup(MemberRequestDto.SignUpDTO request) {

        if (memberRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = MemberConverter.toMember(request, encodedPassword);
        Member savedMember = memberRepository.save(member);

        return MemberConverter.toSignUpResultDTO(savedMember);
    }

    public MemberResponseDto.LoginResultDTO login(MemberRequestDto.LoginDTO request) {

        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(member.getId());

        return MemberConverter.toLoginResultDTO(member, accessToken);
    }
}
