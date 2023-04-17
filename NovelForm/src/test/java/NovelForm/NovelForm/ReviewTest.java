package NovelForm.NovelForm;


import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.domain.member.Review;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

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

    @Test
    @Rollback(false)
    void 리뷰생성1(){
        //given : 카카오라는 유저가 홍길동전, 성냥팔이 소녀 리뷰 작성
        Member findMember = memberRepository.findByNickname("카카오");

        //when 홍길동전, 성냥팔이 소녀에 대한 리뷰 작성. 후 저장
        Novel girls = novelRepository.findById(1L).get();//성냥팔이 소녀
        Novel hong = novelRepository.findById(2L).get(); //홍길동 전

        Review review1 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review1.addMember(findMember);
        review1.addNovel(girls);

        Review review2 = Review.builder()
                .content("박진감 넘쳐요")
                .rating(4.5)
                .build();
        review2.addNovel(hong);
        review2.addMember(findMember);


        Review result1 = reviewRepository.save(review1);
        Review result2 = reviewRepository.save(review2);

        assertThat(result1.getContent()).isEqualTo(review1.getContent());
        assertThat(result2.getContent()).isEqualTo(review2.getContent());
    }

    @Test
    @Rollback(false)
    void 리뷰생성2(){
        //given : 카카오라는 유저가 홍길동전, 성냥팔이 소녀 리뷰 작성
        Member findMember = memberRepository.findByNickname("ssu");

        //when 홍길동전, 성냥팔이 소녀에 대한 리뷰 작성. 후 저장
        Novel girls = novelRepository.findById(1L).get();//성냥팔이 소녀

        Review review1 = Review.builder()
                .content("눈물을 흘렸습니다.")
                .rating(5.0)
                .build();
        review1.addMember(findMember);
        review1.addNovel(girls);


        Review result1 = reviewRepository.save(review1);
        System.out.println("성냥팔이 소녀에 대한 평균 평점 : " + result1.getNovel().averageRating());

        assertThat(result1.getContent()).isEqualTo(review1.getContent());
    }

    @Test
    @DisplayName("리뷰생성1,2 후 성냥팔이 소녀에 대한 리뷰 수가 2개인지 확인")
    void 리뷰개수확인(){
        List<Novel> find = novelRepository.findByTitleName("홍길동전");
        if(find.size() != 1){
            System.out.println("동일한 이름의 소설이 두 개 이상존재.");
            return;
        }
        Novel girl = find.get(0);
        List<Review> reviews = girl.getReviews();
        for (Review review : reviews) {
            System.out.println("작성자 : " + review.getMember().getNickname());
            System.out.println("평점 : " + review.getRating() + " 리뷰 : " + review.getContent());
        }
        assertThat(girl.getReview_cnt()).isEqualTo(2);
    }


}
