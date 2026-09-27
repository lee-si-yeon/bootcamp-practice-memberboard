package com.example.memberboard;

import com.example.memberboard.domain.comment.entity.Comment;
import com.example.memberboard.domain.comment.repository.CommentRepository;
import com.example.memberboard.domain.member.entity.Member;
import com.example.memberboard.domain.member.repository.MemberRepository;
import com.example.memberboard.domain.post.entity.Post;
import com.example.memberboard.domain.post.repository.PostRepository;
import com.example.memberboard.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CommentIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    Member author;
    Post post;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        author = memberRepository.save(Member.builder()
                .email("author@example.com")
                .password("encoded-password")
                .nickname("작성자")
                .build());
        post = postRepository.save(Post.builder()
                .title("게시글")
                .content("본문")
                .author(author)
                .build());
    }

    @Test
    void 로그인한_회원은_댓글을_작성하고_누구나_목록을_조회할_수_있다() throws Exception {
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", post.getId())
                        .header("Authorization", bearer(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"첫 댓글"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorNickname").value("작성자"));

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].content").value("첫 댓글"));

        mockMvc.perform(get("/api/v1/posts?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].commentCount").value(1));
    }

    @Test
    void 댓글_작성자는_댓글을_수정하고_삭제할_수_있다() throws Exception {
        Comment comment = commentRepository.save(comment(author));

        mockMvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
                        .header("Authorization", bearer(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"수정 댓글"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정 댓글"));

        mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
                        .header("Authorization", bearer(author)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 다른_회원의_댓글을_수정하면_403을_반환한다() throws Exception {
        Comment comment = commentRepository.save(comment(author));
        Member other = memberRepository.save(Member.builder()
                .email("other@example.com")
                .password("encoded-password")
                .nickname("다른 회원")
                .build());

        mockMvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
                        .header("Authorization", bearer(other))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"수정 댓글"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void 게시글을_삭제하면_댓글도_함께_삭제된다() throws Exception {
        commentRepository.save(comment(author));

        mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId())
                        .header("Authorization", bearer(author)))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.count()).isZero();
    }

    private Comment comment(Member member) {
        return Comment.builder()
                .content("댓글")
                .author(member)
                .post(post)
                .build();
    }

    private String bearer(Member member) {
        return "Bearer " + jwtTokenProvider.createToken(member.getId());
    }
}
