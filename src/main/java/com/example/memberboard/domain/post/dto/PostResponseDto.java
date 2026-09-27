package com.example.memberboard.domain.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

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

    // 게시글 목록 항목
    @Builder
    public record ListDTO(
            Long postId,
            String title,
            String authorNickname,
            long commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // 게시글 목록 조회 결과
    @Builder
    public record ListResultDTO(
            List<ListDTO> posts,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}
