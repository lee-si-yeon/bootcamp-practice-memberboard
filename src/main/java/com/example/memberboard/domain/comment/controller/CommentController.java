package com.example.memberboard.domain.comment.controller;

import com.example.memberboard.domain.comment.dto.CommentRequestDto;
import com.example.memberboard.domain.comment.dto.CommentResponseDto;
import com.example.memberboard.domain.comment.service.CommentCommandService;
import com.example.memberboard.domain.comment.service.CommentQueryService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.DetailDTO> create(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto.CreateCommentDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentCommandService.create(memberId, postId, request));
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.ListResultDTO> getList(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentQueryService.getList(postId));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto.DetailDTO> update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto.UpdateCommentDTO request
    ) {
        return ResponseEntity.ok(commentCommandService.update(memberId, commentId, request));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long commentId
    ) {
        commentCommandService.delete(memberId, commentId);
        return ResponseEntity.noContent().build();
    }
}
