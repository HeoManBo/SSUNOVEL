package NovelForm.NovelForm;


import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.domain.Review;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ReviewTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AuthorRepository authorRepository;

    /**
     * 해당 클래스 수행후 DB에서
     * delete from review; 수행해야함.
     */


    //각각의 테스트 수행전 기본으로 DB상에 임시로 존재하는 회원, 소설 생성
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
    }

    @Test
    //@Rollback(false)
    void 리뷰생성1(){
        createTestSample();

        //given : ssu라는 유저가 1번, 2번 소설에 대한 리뷰 작성
        Member findMember = memberRepository.findByNickname("ssu");

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

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        Review result1 = reviewRepository.findById(review1.getId()).get();
        Review result2 = reviewRepository.findById(review2.getId()).get();


        assertThat(result1.getContent()).isEqualTo(review1.getContent());
        assertThat(result2.getContent()).isEqualTo(review2.getContent());
    }

    @Test
    //@Rollback(false)
    void 리뷰생성2(){
        createTestSample();

        //given : test라는 유저가 성냥팔이 소녀 리뷰 작성
        Member findMember1 = memberRepository.findByNickname("test");
        Member findMember2 = memberRepository.findByNickname("ssu");

        //when : 성냥팔이 소녀에 대한 리뷰 작성
        List<Novel> girls = novelRepository.findByTitleName("성냥팔이 소녀");

        Review review1 = Review.builder()
                .content("눈물을 흘렸습니다.")
                .rating(5.0)
                .build();
        review1.addMember(findMember1);
        review1.addNovel(girls.get(0));

        Review review2 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review2.addMember(findMember2);
        review2.addNovel(girls.get(0));

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        Review result = reviewRepository.findById(review1.getId()).get();

        assertThat(review1.getContent()).isEqualTo(result.getContent());
        assertThat(result.getNovel().averageRating()).isEqualTo(4.5); // (4.0 + 5.0) / 2 = 4.5;
    }
    @Test
    @DisplayName("리뷰생성1,2 후 소설번호 1번에 대한 리뷰 수가 2개인지 확인")
    void 리뷰개수확인(){
        createTestSample();

        //given : test라는 유저가 성냥팔이 소녀 리뷰 작성
        Member findMember1 = memberRepository.findByNickname("test");
        Member findMember2 = memberRepository.findByNickname("ssu");

        //when : 성냥팔이 소녀에 대한 리뷰 작성
        List<Novel> girls = novelRepository.findByTitleName("성냥팔이 소녀");

        Review review1 = Review.builder()
                .content("눈물을 흘렸습니다.")
                .rating(5.0)
                .build();
        review1.addMember(findMember1);
        review1.addNovel(girls.get(0));

        Review review2 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review2.addMember(findMember2);
        review2.addNovel(girls.get(0));

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        Novel find = novelRepository.findByTitleName("성냥팔이 소녀").get(0);

        List<Review> reviews = find.getReviews();
        System.out.println("성냥팔이 소설에 대한 평균 평점 : " + find.getRating() / find.getReview_cnt());
        for (Review review : reviews) {
            System.out.println("작성자 : " + review.getMember().getNickname());
            System.out.println("평점 : " + review.getRating() + " 리뷰 : " + review.getContent());
        }

        assertThat(find.getReview_cnt()).isEqualTo(2);
    }


}
