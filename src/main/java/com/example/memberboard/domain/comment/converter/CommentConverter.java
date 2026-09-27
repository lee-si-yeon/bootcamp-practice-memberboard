package com.example.memberboard.domain.comment.converter;

import com.example.memberboard.domain.comment.dto.CommentRequestDto;
import com.example.memberboard.domain.comment.dto.CommentResponseDto;
import com.example.memberboard.domain.comment.entity.Comment;
import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.post.entity.Post;

import java.util.List;

public class CommentConverter {

    // DTO -> 댓글 엔티티
    public static Comment toComment(CommentRequestDto.CreateCommentDTO request, Member author, Post post) {
        return Comment.builder()
                .content(request.content())
                .author(author)
                .post(post)
                .build();
    }

    // entity -> 댓글 상세 DTO
    public static CommentResponseDto.DetailDTO toDetailDTO(Comment comment) {
        return CommentResponseDto.DetailDTO.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorNickname(comment.getAuthor().getNickname())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // entity 목록 -> 댓글 목록 조회 결과 DTO
    public static CommentResponseDto.ListResultDTO toListResultDTO(List<Comment> comments) {
        return CommentResponseDto.ListResultDTO.builder()
                .comments(comments.stream().map(CommentConverter::toDetailDTO).toList())
                .build();
    }
}
