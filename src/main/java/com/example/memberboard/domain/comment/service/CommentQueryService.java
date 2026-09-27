package com.example.memberboard.domain.comment.service;

import com.example.memberboard.domain.comment.converter.CommentConverter;
import com.example.memberboard.domain.comment.dto.CommentResponseDto;
import com.example.memberboard.domain.comment.repository.CommentRepository;
import com.example.memberboard.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 게시글의 댓글 목록 조회
    public CommentResponseDto.ListResultDTO getList(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return CommentConverter.toListResultDTO(commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId));
    }
}
