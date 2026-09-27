package com.example.memberboard;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PostIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    Member author;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
        author = memberRepository.save(Member.builder()
                .email("author@example.com")
                .password("encoded-password")
                .nickname("작성자")
                .build());
    }

    @Test
    void 로그인한_회원은_게시글을_작성하고_누구나_상세_조회할_수_있다() throws Exception {
        String token = jwtTokenProvider.createToken(author.getId());

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"첫 게시글","content":"게시글 본문"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("첫 게시글"))
                .andExpect(jsonPath("$.authorNickname").value("작성자"));

        Long postId = postRepository.findAll().getFirst().getId();
        mockMvc.perform(get("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("게시글 본문"))
                .andExpect(jsonPath("$.authorId").value(author.getId()));
    }

    @Test
    void 인증_없이_게시글을_작성하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"제목","content":"본문"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void 없는_게시글을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void 게시글_목록은_최신순으로_페이지_조회된다() throws Exception {
        postRepository.saveAndFlush(newPost("이전 글"));
        postRepository.saveAndFlush(newPost("최신 글"));

        mockMvc.perform(get("/api/v1/posts?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].title").value("최신 글"))
                .andExpect(jsonPath("$.posts[0].authorNickname").value("작성자"))
                .andExpect(jsonPath("$.posts[0].commentCount").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void 잘못된_페이지_요청은_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/posts?page=-1&size=10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void 작성자는_게시글을_수정하고_삭제할_수_있다() throws Exception {
        Post post = postRepository.save(newPost("수정 전"));
        String token = jwtTokenProvider.createToken(author.getId());

        mockMvc.perform(put("/api/v1/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"수정 후","content":"수정 본문"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 후"));

        mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void 다른_회원의_게시글을_수정하면_403을_반환한다() throws Exception {
        Post post = postRepository.save(newPost("작성자 글"));
        Member other = memberRepository.save(Member.builder()
                .email("other@example.com")
                .password("encoded-password")
                .nickname("다른 회원")
                .build());

        mockMvc.perform(put("/api/v1/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(other.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"수정","content":"수정 본문"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    private Post newPost(String title) {
        return Post.builder()
                .title(title)
                .content("본문")
                .author(author)
                .build();
    }
}
