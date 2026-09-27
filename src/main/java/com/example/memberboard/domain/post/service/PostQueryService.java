package com.example.memberboard.domain.post.service;

import com.example.memberboard.domain.post.converter.PostConverter;
import com.example.memberboard.domain.post.dto.PostResponseDto;
import com.example.memberboard.domain.post.entity.Post;
import com.example.memberboard.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;

    // 게시글 상세 조회
    public PostResponseDto.DetailDTO get(Long postId) {
        Post post = postRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        return PostConverter.toDetailDTO(post);
    }

    // 게시글 목록 조회
    public PostResponseDto.ListResultDTO getList(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);

        return PostConverter.toListResultDTO(posts);
    }
}
