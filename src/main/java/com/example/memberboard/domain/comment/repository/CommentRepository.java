package com.example.memberboard.domain.comment.repository;

import com.example.memberboard.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "post"})
    Optional<Comment> findWithAuthorAndPostById(Long id);

    @EntityGraph(attributePaths = "author")
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
