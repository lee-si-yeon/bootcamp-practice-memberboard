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
}
