package com.example.memberboard.domain.post.service;

import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.member.repository.MemberRepository;
import com.example.memberboard.domain.post.converter.PostConverter;
import com.example.memberboard.domain.post.dto.PostRequestDto;
import com.example.memberboard.domain.post.dto.PostResponseDto;
import com.example.memberboard.domain.post.entity.Post;
import com.example.memberboard.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 작성
    public PostResponseDto.DetailDTO create(Long memberId, PostRequestDto.CreatePostDTO request) {
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        Post post = postRepository.save(PostConverter.toPost(request, author));

        return PostConverter.toDetailDTO(post);
    }

    // 게시글 수정
    public PostResponseDto.DetailDTO update(Long memberId, Long postId, PostRequestDto.UpdatePostDTO request) {
        Post post = getOwnedPost(memberId, postId);
        post.update(request.title(), request.content());

        return PostConverter.toDetailDTO(post);
    }

    // 게시글 삭제
    public void delete(Long memberId, Long postId) {
        postRepository.delete(getOwnedPost(memberId, postId));
    }

    private Post getOwnedPost(Long memberId, Long postId) {
        Post post = postRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 수정하거나 삭제할 수 있습니다.");
        }
        return post;
    }
}
