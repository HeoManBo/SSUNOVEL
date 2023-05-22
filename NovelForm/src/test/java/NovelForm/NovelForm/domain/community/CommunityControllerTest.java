package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.dto.WriteDto;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 커뮤니티 컨트롤러 테스트
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Slf4j
public class CommunityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 10명의 회원이 있다고 가정한다.
     * 그리고 각 회원이 5명의 게시글을 작성했다고 가정
     */
    @BeforeEach
    void MakeExample(){
        // 10명의 회원 생성
        List<Member> members = new ArrayList<>();
        for(int i=1; i<=10; i++){
            Member member = Member.builder()
                    .email(i + "@naver.com")
                    .password("1234")
                    .nickname("ssu" + i)
                    .gender(Gender.MALE)
                    .loginType(LoginType.USER)
                    .build();
            members.add(member);
        }

        List<CommunityPost> communities = new ArrayList<>();

        // 10 * 5, 총 50개의 게시글을 생성한다.
        for(int i=0; i<10; i++){
            for(int j=1; j<=5; j++) {
                CommunityPost communityPost = CommunityPost.builder()
                        .title((i + 1) + "번째유저의" + j + "번째 글")
                        .content("test")
                        .build();
                communityPost.addMember(members.get(i));
                communities.add(communityPost);
            }
        }

        //저장한다
        memberRepository.saveAll(members);
        communityPostRepository.saveAll(communities);

        em.flush();
        em.clear();
    }


    @Test
    void 게시글조회테스트() throws Exception{
        //then : before Each 수행

        //when : 맨 처음 커뮤니티 탭을 눌렀을 때, 가장 최신 10개 게시글이 나오는지 확인한다.
       mockMvc.perform(MockMvcRequestBuilders.get("/community"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());;

        //when : 마지막 페이지 번호를 조회해본다.
       mockMvc.perform(MockMvcRequestBuilders.get("/community")
                       .param("page", "4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());;

    }

    @Test
    void 게시글조회테스트_에러케이스() throws Exception{
        //then : before Each 수행

        //when : page 번호의 값을 음수로 넘겨본다.
       mockMvc.perform(MockMvcRequestBuilders.get("/community")
                .param("page", "-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : page 번호의 최대 값을 넘어서는 값으로 조회한다.
        mockMvc.perform(MockMvcRequestBuilders.get("/community")
                        .param("page", "9999"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : page 번호가 string으로 넘어오는 경우
        mockMvc.perform(MockMvcRequestBuilders.get("/community")
                        .param("page", "asdf"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 게시글생성테스트() throws Exception {
        WriteDto writeDto = new WriteDto("test","test");
        WriteDto noTitle = new WriteDto("", "test");
        WriteDto noContent = new WriteDto("test", "");
        //세션 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(writeDto))
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());


        //when 에러 1:로그인하지 않는 유저가 게시글 작성을 시도하는 경우 --> 세션이 없는 경우
        mockMvc.perform(MockMvcRequestBuilders.post("/community")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : 에러 2 : 제목이 없는 게시글을 작성하려는 경우
        mockMvc.perform(MockMvcRequestBuilders.post("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noTitle))
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : 에러 3 : 내용이 없는 게시글을 작성하려는 경우
        mockMvc.perform(MockMvcRequestBuilders.post("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noContent))
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }


    @Test
    void 게시글수정테스트() throws Exception {
        //given : 1번 유저와 3번 게시글이 주어졌을 때,
        WriteDto newWrite = new WriteDto("aaaa","aaaa");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        //when : 정상 로직 1. 3번 게시글을 수정한다.
        mockMvc.perform(MockMvcRequestBuilders.patch("/community/{post_id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newWrite)).session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        em.flush();
        em.clear();
        //then : DB에 수정된 결과 값을 비교해본다.
        CommunityPost communityPost = communityPostRepository.findById(1L).get();

        assertThat(communityPost.getTitle()).isEqualTo(newWrite.getTitle());
        assertThat(communityPost.getContent()).isEqualTo(newWrite.getContent());

        //when : 에러 1 : 1번유저가 자신이 작성하지 않은 게시글 수정 시도
        mockMvc.perform(MockMvcRequestBuilders.patch("/community/{post_id}", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWrite)).session(session))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(print());

        //when : 에러 2 : 세션이 없는 유저가 게시글 수정 시도
        mockMvc.perform(MockMvcRequestBuilders.patch("/community/{post_id}", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWrite)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : 에러 3 : postId가 DB에 없는 경우 수정 시도 --> 잘못된 PathVariable
        mockMvc.perform(MockMvcRequestBuilders.patch("/community/{post_id}", "300")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWrite)).session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

    }


    @Test
    void 게시글삭제테스트() throws Exception {
        //given : 1번유저가 자신의 4번 게시글 삭제
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);


        //when : 정상로직 1 : 4번 게시글 삭제 성공
        mockMvc.perform(MockMvcRequestBuilders.delete("/community/{post_id}", "1")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("삭제 완료"))
                .andDo(print());

        em.flush();
        em.clear();

        //then : DB의 전체 게시글 개수개 49개인지 확인한다.
        long count = communityPostRepository.count();
        assertThat(count).isEqualTo(49);

        //when : 에러로직 1 : 세션이 없음.
        mockMvc.perform(MockMvcRequestBuilders.delete("/community/{post_id}", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

        //when : 에러로직 2 : 다른 사람의 게시글을 삭제하려는 경우
        mockMvc.perform(MockMvcRequestBuilders.delete("/community/{post_id}", "20")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(print());

        //when : 에러로직 2 : 없는 게시글을 지우려는 경우
        mockMvc.perform(MockMvcRequestBuilders.delete("/community/{post_id}", "100")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

    }


    @Test
    void 게시글상세조회테스트() throws Exception {
        //given : 6번 게시글이 존재할때,
        CommunityPost communityPost = communityPostRepository.findById(6L).get();

        //5번 유저가 5개의 댓글을 달았다고 가정한다. 단순화를 위해 한명이 5개 댓글을 달았다고 하자.
        Member fiveMember = memberRepository.findById(5L).get();


        for(int i=1; i<=5; i++){
            Comment comment = Comment.builder()
                    .content(i +"comment").build();
            comment.addCommunityPost(communityPost);
            comment.addMember(fiveMember);
            commentRepository.save(comment);
        }

        em.flush();
        em.clear();

        log.info("============================================");

        //when : 정상 로직
        mockMvc.perform(MockMvcRequestBuilders.get("/community/{post_id}", "6"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        //when : 비정상 로직 : 없는 POSTID 게시글 조회
        mockMvc.perform(MockMvcRequestBuilders.get("/community/{post_id}", "300"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());

    }


    @Test
    void 게시글검색테스트() throws Exception {

        //given : keyword = 1번째 제목
        String keyword = "1번째";

        //when : 해당 검색어로 조회해본다
        mockMvc.perform(MockMvcRequestBuilders.get("/community/search")
                        .param("keyword",keyword))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

    }

}
