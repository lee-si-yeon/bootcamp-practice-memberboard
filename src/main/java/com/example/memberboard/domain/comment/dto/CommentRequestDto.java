package com.example.memberboard.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequestDto {

    // 댓글 작성
    public record CreateCommentDTO(
            @NotBlank(message = "댓글 본문은 필수입니다.")
            String content
    ) {}

    // 댓글 수정
    public record UpdateCommentDTO(
            @NotBlank(message = "댓글 본문은 필수입니다.")
            String content
    ) {}
}
