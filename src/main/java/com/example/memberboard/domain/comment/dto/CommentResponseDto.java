package com.example.memberboard.domain.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDto {

    // 댓글 작성 및 조회 결과
    @Builder
    public record DetailDTO(
            Long commentId,
            Long postId,
            String content,
            Long authorId,
            String authorNickname,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // 댓글 목록 조회 결과
    @Builder
    public record ListResultDTO(
            List<DetailDTO> comments
    ) {}
}
