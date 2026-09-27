package com.example.memberboard.domain.post.repository;

import com.example.memberboard.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "author")
    Optional<Post> findWithAuthorById(Long id);

    @Query(
            value = """
                    select p.id as postId, p.title as title, p.author.nickname as authorNickname,
                           count(c.id) as commentCount, p.createdAt as createdAt, p.updatedAt as updatedAt
                    from Post p left join p.comments c
                    group by p.id, p.title, p.author.nickname, p.createdAt, p.updatedAt
                    """,
            countQuery = "select count(p) from Post p"
    )
    Page<PostListProjection> findPostList(Pageable pageable);

    interface PostListProjection {
        Long getPostId();
        String getTitle();
        String getAuthorNickname();
        long getCommentCount();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();
    }
}
