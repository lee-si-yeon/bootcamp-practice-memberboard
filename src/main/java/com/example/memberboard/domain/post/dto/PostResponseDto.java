package com.example.memberboard.domain.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class PostResponseDto {

    // 게시글 작성 및 상세 조회 결과
    @Builder
    public record DetailDTO(
            Long postId,
            String title,
            String content,
            Long authorId,
            String authorNickname,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
