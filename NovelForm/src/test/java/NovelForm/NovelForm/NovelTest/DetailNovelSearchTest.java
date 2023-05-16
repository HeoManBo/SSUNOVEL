package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.review.domain.Review;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 *  NovelSearchContoller.detailSearchNovel 함수 테스트 클래스입니다.
 */

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class DetailNovelSearchTest {
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
    private FavoriteNovelRepository favoriteNovelRepository;

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
                .summary("홍길동")
                .cover_image("Image2@test.co.kr")
                .is_kakao("kakakakakak")
                .is_munpia("munmunmunmun")
                .category("판타지")
                .build();
        novel1.addAuthor(author);

        Novel novel2 = Novel.builder()
                .title("성냥팔이 소녀")
                .download_cnt(0)
                .rating(0.0)
                .review_cnt(0)
                .is_ridi("ridiridiridi")
                .is_naver("navernaver")
                .summary("성냥")
                .cover_image("Image@test.co.kr")
                .category("무협")
                .build();
        novel2.addAuthor(author);

        novelRepository.save(novel1);
        novelRepository.save(novel2);
    }

    @BeforeEach
    void 리뷰생성1(){
        createTestSample();

        //given : ssu라는 유저가 1번, 2번 소설에 대한 리뷰 작성
        //test라는 유저가 성냥팔이에 대한 리뷰 작성
        Member findMember = memberRepository.findByNickname("ssu");
        Member findMember1 = memberRepository.findByNickname("test");

        //when 홍길동전, 성냥팔이 소녀에 대한 리뷰 작성. 후 저장
        List<Novel> girls = novelRepository.findByTitleName("성냥팔이 소녀");
        List<Novel> hong = novelRepository.findByTitleName("홍길동전");

        Review review1 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review1.addMember(findMember);
        review1.addNovel(girls.get(0));

        Review review2 = Review.builder()
                .content("박진감 넘쳐요")
                .rating(4.5)
                .build();
        review2.addNovel(hong.get(0));
        review2.addMember(findMember);


        Review review3 = Review.builder()
                        .content("너무 슬퍼요")
                                .rating(4.0)
                                        .build();
        review3.addMember(findMember1);
        review3.addNovel(girls.get(0));

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        em.flush();
        em.clear();
    }

    /**
     * 로그인을 한 성냥팔이 소녀에 대한 리뷰 작성 후 상세 소설 정보 조회 즐겨찾기는 등록 X
     */
    //NovelSearchContoller.detailSearchNovel 함수 테스트
    @Test
    public void 상세소설정보조회_로그인_버전() throws Exception {
        //given : @beforeEach 수행

        //when : ssu 유저가 로그인한 상태에서 성냥팔이소녀 소설 상세 조회
        List<Novel> novels = novelRepository.findByTitleName("성냥팔이 소녀");
        Long novel_id = novels.get(0).getId(); //쿼리 파라미터로 전달할 소설 번호 전달
        Member member = memberRepository.findByNickname("ssu");
        Long member_id = member.getId();

        //세션 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, member_id);

        String url = "/novel/" + novel_id.intValue();
        System.out.println("url : " + url);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .session(session));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.title").value("성냥팔이 소녀"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value("성냥"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.category").value("무협"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_review").value("재미있어요"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.review_cnt").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.alreadyLike").value(0));;
    }

    /**
     * 로그인을 하지 않은 사람이 성냥팔이 소녀에 대한 상세 소설 정보 조회
     */
    @Test
    public void 상세소설정보조회_비로그인_버전() throws Exception {
        //given : @beforeEach 수행

        //when : ssu 유저가 로그인한 상태에서 성냥팔이소녀 소설 상세 조회
        List<Novel> novels = novelRepository.findByTitleName("성냥팔이 소녀");
        Long novel_id = novels.get(0).getId(); //쿼리 파라미터로 전달할 소설 번호 전달
        Member member = memberRepository.findByNickname("ssu");
        Long member_id = member.getId();

        String url = "/novel/" + novel_id.intValue();

        //비로그인 버전이므로 세션이 존재하지 않음.
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.title").value("성냥팔이 소녀"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value("성냥"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.category").value("무협"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_rating").value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.review_cnt").value(2));
    }

    /**
     * 로그인을 한 사람이 성냥팔이 소녀에 대한 리뷰 작성 및 좋아요를 한 경우의 테스트
     */
    @Test
    public void 상세소설정보조회_로그인_좋아요_버전() throws Exception {
        //given : @beforeEach 수행

        //when 1 : ssu 유저가 로그인한 상태에서 성냥팔이소녀 소설 상세 조회
        List<Novel> novels = novelRepository.findByTitleName("성냥팔이 소녀");
        Long novel_id = novels.get(0).getId(); //쿼리 파라미터로 전달할 소설 번호 전달
        Member member = memberRepository.findByNickname("ssu");
        Long member_id = member.getId();

        //when 2 : ssu 유저가 성냥팔이 소녀를 즐겨찾기 했다고 가정,
        FavoriteNovel favoriteNovel = new FavoriteNovel();
        favoriteNovel.addNovel(novels.get(0));
        favoriteNovel.addMember(member);
        favoriteNovelRepository.save(favoriteNovel);

        //세션 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, member_id);

        String url = "/novel/" + novel_id.intValue();
        System.out.println("url : " + url);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .session(session));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.title").value("성냥팔이 소녀"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value("성냥"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.category").value("무협"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_review").value("재미있어요"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.review_cnt").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.alreadyLike").value(1));
    }

    /**
     * 로그인 한 유저가  리뷰를 작성하지 않은 경우, 좋아요도 X
     */
    @Test
    //@Rollback(false)
    public void 상세소설정보조회_로그인_리뷰미작성() throws Exception {
        //given : @beforeEach 수행

        //when : test 유저가 로그인한 상태에서 홍길동전 소설 상세 조회 -> beforeEach에서 test 유저는 홍길동전에 리뷰를 남기지 않음.
        List<Novel> novels = novelRepository.findByTitleName("홍길동전");
        Long novel_id = novels.get(0).getId(); //쿼리 파라미터로 전달할 소설 번호 전달
        Member member = memberRepository.findByNickname("test");
        Long member_id = member.getId();

        //세션 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, member_id);

        String url = "/novel/" + novel_id.intValue();
        System.out.println("url : " + url);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .session(session));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.title").value("홍길동전"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value("홍길동"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.category").value("판타지"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_rating").value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.review_cnt").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.alreadyLike").value(0));
    }

    /**
     * 로그인한 유저가 리뷰작성은 X, 즐겨찾기만 함.
     */
    @Test
    public void 상세소설정보조회_로그인_리뷰미작성_즐겨찾기O() throws Exception {
        //given : @beforeEach 수행

        //when : test 유저가 로그인한 상태에서 홍길동전 소설 상세 조회 -> beforeEach에서 test 유저는 홍길동전에 리뷰를 남기지 않음.
        List<Novel> novels = novelRepository.findByTitleName("홍길동전");
        Long novel_id = novels.get(0).getId(); //쿼리 파라미터로 전달할 소설 번호 전달
        Member member = memberRepository.findByNickname("test");
        Long member_id = member.getId();

        //즐겨찾기 등록
        FavoriteNovel favoriteNovel = new FavoriteNovel();
        favoriteNovel.addNovel(novels.get(0));
        favoriteNovel.addMember(member);
        favoriteNovelRepository.save(favoriteNovel);

        //세션 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_MEMBER_ID, member_id);

        String url = "/novel/" + novel_id.intValue();
        System.out.println("url : " + url);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .session(session));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.title").value("홍길동전"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value("홍길동"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.category").value("판타지"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.authorName").value("숭숭숭"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.my_rating").value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.review_cnt").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.alreadyLike").value(1));
    }

    /**
     * 존재하지 않은 소설 번호를 파라미터값으로 전달한 경우
     */
    @Test
    public void 소설번호_에러테스트() throws Exception {
        //given 123123123번 소설 검색
        String url = "/novel/123123123";
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url));

        // 에러 메세지 API에 맞게 변환됐는지 확인
        result.andDo(print())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("BAD_REQUEST"));
    }



}
