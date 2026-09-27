package com.example.memberboard.domain.post.converter;

import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.post.dto.PostRequestDto;
import com.example.memberboard.domain.post.dto.PostResponseDto;
import com.example.memberboard.domain.post.entity.Post;
import com.example.memberboard.domain.post.repository.PostRepository.PostListProjection;
import org.springframework.data.domain.Page;

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

    // entity -> 게시글 목록 DTO
    public static PostResponseDto.ListDTO toListDTO(PostListProjection post) {
        return PostResponseDto.ListDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .authorNickname(post.getAuthorNickname())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // page -> 게시글 목록 조회 결과 DTO
    public static PostResponseDto.ListResultDTO toListResultDTO(Page<PostListProjection> posts) {
        return PostResponseDto.ListResultDTO.builder()
                .posts(posts.map(PostConverter::toListDTO).getContent())
                .page(posts.getNumber())
                .size(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .build();
    }
}
