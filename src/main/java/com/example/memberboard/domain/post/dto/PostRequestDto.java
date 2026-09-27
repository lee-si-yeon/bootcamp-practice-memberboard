package com.example.memberboard.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequestDto {

    // 게시글 작성
    public record CreatePostDTO(
            @NotBlank(message = "제목은 필수입니다.")
            @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
            String title,

            @NotBlank(message = "본문은 필수입니다.")
            String content
    ) {}
}
