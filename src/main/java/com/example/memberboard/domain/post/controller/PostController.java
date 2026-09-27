package com.example.memberboard.domain.post.controller;

import com.example.memberboard.domain.post.dto.PostRequestDto;
import com.example.memberboard.domain.post.dto.PostResponseDto;
import com.example.memberboard.domain.post.service.PostCommandService;
import com.example.memberboard.domain.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<PostResponseDto.DetailDTO> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody PostRequestDto.CreatePostDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postCommandService.create(memberId, request));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto.DetailDTO> get(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postQueryService.get(postId));
    }
}
