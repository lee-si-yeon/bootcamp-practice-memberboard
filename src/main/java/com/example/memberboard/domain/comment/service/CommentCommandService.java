package com.example.memberboard.domain.comment.service;

import com.example.memberboard.domain.comment.converter.CommentConverter;
import com.example.memberboard.domain.comment.dto.CommentRequestDto;
import com.example.memberboard.domain.comment.dto.CommentResponseDto;
import com.example.memberboard.domain.comment.entity.Comment;
import com.example.memberboard.domain.comment.repository.CommentRepository;
import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.member.repository.MemberRepository;
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
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    public CommentResponseDto.DetailDTO create(Long memberId, Long postId, CommentRequestDto.CreateCommentDTO request) {
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.save(CommentConverter.toComment(request, author, post));

        return CommentConverter.toDetailDTO(comment);
    }

    // 댓글 수정
    public CommentResponseDto.DetailDTO update(
            Long memberId,
            Long commentId,
            CommentRequestDto.UpdateCommentDTO request
    ) {
        Comment comment = getOwnedComment(memberId, commentId);
        comment.update(request.content());

        return CommentConverter.toDetailDTO(comment);
    }

    // 댓글 삭제
    public void delete(Long memberId, Long commentId) {
        commentRepository.delete(getOwnedComment(memberId, commentId));
    }

    private Comment getOwnedComment(Long memberId, Long commentId) {
        Comment comment = commentRepository.findWithAuthorAndPostById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정하거나 삭제할 수 있습니다.");
        }
        return comment;
    }
}
