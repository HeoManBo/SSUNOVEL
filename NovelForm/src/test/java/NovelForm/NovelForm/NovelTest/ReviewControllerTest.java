package NovelForm.NovelForm.NovelTest;

import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.ReviewDto;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Slf4j
@Transactional
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;


    //각각의 테스트 수행전 기본으로 DB상에 임시로 존재하는 회원, 소설 생성
    @BeforeEach
    void createTestSample(){
        Member member = Member.builder()
                .email("test@naver.com")
                .password("1234")
                .nickname("ssu")
                .gender(Gender.MALE)
                .loginType(LoginType.USER)
                .build();

        memberRepository.save(member);

        Member member2 = Member.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickname("test")
                .gender(Gender.MALE)
                .loginType(LoginType.USER)
                .build();

        memberRepository.save(member2);

        Author author = Author.builder()
                .name("숭숭숭")
                .build();
        authorRepository.save(author);

        Novel novel1 = Novel.builder()
                .title("홍길동전")
                .download_cnt(0)
                .rating(0.0)
                .review_cnt(0)
                .summary("테스트 입력")
                .build();
        novel1.addAuthor(author);

        Novel novel2 = Novel.builder()
                .title("성냥팔이 소녀")
                .download_cnt(0)
                .rating(0.0)
                .review_cnt(0)
                .summary("테스트 입력")
                .build();
        novel2.addAuthor(author);

        novelRepository.save(novel1);
        novelRepository.save(novel2);

        Review review1 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review1.addMember(member);
        review1.addNovel(novel1);

        Review review2 = Review.builder()
                .content("박진감 넘쳐요")
                .rating(4.5)
                .build();
        review2.addNovel(novel2);
        review2.addMember(member);

        Review review3 = Review.builder()
                .content("꿀잼")
                .rating(3.5)
                .build();
        review3.addNovel(novel2);
        review3.addMember(member2);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        em.flush();
        em.clear();
    }

    @Test
    void 리뷰생성테스트_정상로직() throws Exception {

        //when : 정상로직 : 2번유저가 홍길동전 리뷰 작성
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 2L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(0.5, "test");

        mockMvc.perform(MockMvcRequestBuilders.post("/novel/review/{novel_id}", 1L)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isOk()) //then : http -> isOK
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(4L)) //then -> 현재 리뷰가 3개밖에 없으므로 생성된 리뷰 id는 4가 되어야 함.
                .andDo(print());

    }
    @Test
    void 리뷰생성테스트_비정상로직1() throws Exception {
        //given : 세션값 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 2L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(0.5, "test");

        //when : 비정상로직 1 : 2번유저가 이미 작성한 성냥팔이소녀에 리뷰 또 작성 시도
        mockMvc.perform(MockMvcRequestBuilders.post("/novel/review/{novel_id}", 2L)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) //then : http -> isBadRequest
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("잘못된 리뷰 등록입니다."));
    }
    @Test
    void 리뷰생성테스트_비정상로직2() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 33333L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(0.5, "test");

        //when : 비정상로직 2 : 존재하지 않는 세션 값을 가진 유저가 리뷰 등록
        mockMvc.perform(MockMvcRequestBuilders.post("/novel/review/{novel_id}", 2L)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError()) //then : http -> isInternalServerError
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("잘못된 유저 아이디입니다."))
                .andDo(print());
    }

    @Test
    void 리뷰생성테스트_비정상로직3() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 2L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(0.5, "test");

        //when : 비정상로직 3 : 존재하지 않는 소설에 리뷰 작성
        mockMvc.perform(MockMvcRequestBuilders.post("/novel/review/{novel_id}", 3000L)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) //then : http -> isBadRequest
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("해당하는 소설이 존재하지 않습니다."))
                .andDo(print());
    }

    @Test
    void 리뷰수정테스트_정상로직() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(5.0, "excellent");

        //when : 1번유저가 자신이 홍길동전 리뷰 수정
        mockMvc.perform(MockMvcRequestBuilders.patch("/novel/review/{novel_id}/{review_id}", 1, 1)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("success"))
                .andDo(print());

        Novel novel = novelRepository.findByTitleName("홍길동전").get(0);
        Member member = memberRepository.findById(1L).get();

        em.flush();
        em.clear();

        Review findReview = reviewRepository.findSingleReivew(member, novel).get();

        //DB에 잘 저장됐는지 확인한다.
        assertThat(findReview.getContent()).isEqualTo(reviewDto.getContent());
        assertThat(findReview.getRating()).isEqualTo(reviewDto.getRating());
    }

    @Test
    void 리뷰수정테스트_비정상로직1() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(5.0, "excellent");

        //when : 1번유저가 자신의 리뷰가 아닌 리뷰를 수정
        mockMvc.perform(MockMvcRequestBuilders.patch("/novel/review/{novel_id}/{review_id}", 2, 3)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("해당 리뷰 삭제권한이 없습니다."))
                .andDo(print());
    }

    @Test
    void 리뷰수정테스트_비정상로직2() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(5.0, "excellent");

        //when : 존재하지 않는 소설의 리뷰를 수정하려함
        mockMvc.perform(MockMvcRequestBuilders.patch("/novel/review/{novel_id}/{review_id}", 1111, 3)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("해당하는 소설이 존재하지 않습니다."))
                .andDo(print());
    }

    @Test
    void 리뷰수정테스트_비정상로직3() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);
        ReviewBodyDto reviewDto = new ReviewBodyDto(5.0, "excellent");

        //when : 소설내의 존재하지 않는 리뷰를 삭제하려 함.
        mockMvc.perform(MockMvcRequestBuilders.patch("/novel/review/{novel_id}/{review_id}", 1, 300)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("잘못된 유저 아이디입니다."))
                .andDo(print());
    }

    @Test
    void 리뷰삭제테스트_정상로직() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        //when : 1번 유저(ssu)가 성냥팔이 소녀 리뷰를 삭제한다.
        mockMvc.perform(MockMvcRequestBuilders.delete("/novel/review/{novel_id}/{review_id}", 2L, 2L)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("success"))
                .andDo(print());

        em.flush();
        em.clear();


        //then : 성녕팔이 소녀 소설을 조회 했을 때, 리뷰 수가 한 개인지 확인한다.
        Novel result = novelRepository.findByNovelIdWithReviews(2L);
        assertThat(result.getReviews().size()).isEqualTo(1);
    }


    @Test
    void 리뷰삭제테스트_비정상로직() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        //when : 1번 유저(ssu)가 성냥팔이 소녀 리뷰의 다른 유저(test)의 성냥팔이 소녀 리뷰를 삭제하려는 경우
        mockMvc.perform(MockMvcRequestBuilders.delete("/novel/review/{novel_id}/{review_id}", 2L, 3L)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("해당 리뷰 삭제권한이 없습니다."))
                .andDo(print());

    }

    @Test
    void 리뷰삭제테스트_비정상로직2() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 100L);

        //when : 잘못된 세션 값
        mockMvc.perform(MockMvcRequestBuilders.delete("/novel/review/{novel_id}/{review_id}", 2L, 3L)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("잘못된 유저 아이디입니다."))
                .andDo(print());

    }


    @Test
    void 리뷰삭제테스트_비정상로직3() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        //when : 존재하지 않는 소설의 리뷰 삭제
        mockMvc.perform(MockMvcRequestBuilders.delete("/novel/review/{novel_id}/{review_id}", 200L, 3L)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("해당하는 소설이 존재하지 않습니다."))
                .andDo(print());

    }

    @Test
    void 리뷰삭제테스트_비정상로직4() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, 1L);

        //when : 존재하지 않는 리뷰 번호의 리뷰 삭제
        mockMvc.perform(MockMvcRequestBuilders.delete("/novel/review/{novel_id}/{review_id}", 1L, 1000L)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("잘못된 리뷰 번호입니다."))
                .andDo(print());

    }











}
