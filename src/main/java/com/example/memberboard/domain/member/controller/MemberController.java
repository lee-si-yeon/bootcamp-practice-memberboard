package com.example.memberboard.domain.member.controller;

import com.example.memberboard.domain.member.dto.MemberRequestDto;
import com.example.memberboard.domain.member.dto.MemberResponseDto;
import com.example.memberboard.domain.member.service.MemberCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberCommandService memberCommandService;

    @Operation(summary = "일반 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto.SignUpResultDTO> signup(
            @Valid @RequestBody MemberRequestDto.SignUpDTO request
    ) {
        MemberResponseDto.SignUpResultDTO response = memberCommandService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto.LoginResultDTO> login(
            @Valid @RequestBody MemberRequestDto.LoginDTO request
    ) {
        return ResponseEntity.ok(memberCommandService.login(request));
    }
}
