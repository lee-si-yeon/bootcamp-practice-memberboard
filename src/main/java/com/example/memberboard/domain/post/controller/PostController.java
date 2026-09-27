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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PostResponseDto.ListResultDTO> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page는 0 이상, size는 1 이상 100 이하여야 합니다.");
        }
        return ResponseEntity.ok(postQueryService.getList(page, size));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto.DetailDTO> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto.UpdatePostDTO request
    ) {
        return ResponseEntity.ok(postCommandService.update(memberId, postId, request));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId
    ) {
        postCommandService.delete(memberId, postId);
        return ResponseEntity.noContent().build();
    }
}
