package com.example.memberboard.domain.post.repository;

import com.example.memberboard.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "author")
    Optional<Post> findWithAuthorById(Long id);

    @Override
    @EntityGraph(attributePaths = "author")
    Page<Post> findAll(Pageable pageable);
}
