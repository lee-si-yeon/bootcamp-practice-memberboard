package com.example.memberboard.domain.post.converter;

import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.post.dto.PostRequestDto;
import com.example.memberboard.domain.post.dto.PostResponseDto;
import com.example.memberboard.domain.post.entity.Post;

public class PostConverter {

    // DTO -> 게시글 엔티티
    public static Post toPost(PostRequestDto.CreatePostDTO request, Member author) {
        return Post.builder()
                .title(request.title())
                .content(request.content())
                .author(author)
                .build();
    }

    // entity -> 게시글 상세 DTO
    public static PostResponseDto.DetailDTO toDetailDTO(Post post) {
        return PostResponseDto.DetailDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor().getId())
                .authorNickname(post.getAuthor().getNickname())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
