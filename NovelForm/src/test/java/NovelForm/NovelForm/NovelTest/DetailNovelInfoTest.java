package NovelForm.NovelForm.NovelTest;


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
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상세 소설 조회 테스트입니다.
 */

@SpringBootTest
@Slf4j
@Transactional
public class DetailNovelInfoTest {
    @Autowired
    private  NovelRepository novelRepository;
    @Autowired
    private  EntityManager em;
    @Autowired
    private  AuthorRepository authorRepository;
    @Autowired
    private  MemberRepository memberRepository;
    @Autowired
    private  ReviewRepository reviewRepository;




    void makeReview(Novel novel, Member member, int i){
        Review review = Review.builder()
                .content(i + "너무 재밌어요")
                .rating(5.0).build();
        review.addNovel(novel);
        review.addMember(member);
        reviewRepository.save(review);
    }

    @BeforeEach
    void setting_novel(){
        Author author = new Author("SSU");
        Novel novel = Novel.builder()
                .title("콩쥐 팥쥐")
                .category("판타지")
                .is_kakao(1)
                .is_munpia(1)
                .summary("테스트 summary")
                .cover_image("asdf1234")
                .build();
        novel.addAuthor(author);
        authorRepository.save(author);
        novelRepository.save(novel);
        // 2명의 member 생성
        Member member1 = new Member("aaa", "aaaa", "A", Gender.MALE, LoginType.USER, 5);
        Member member2 = new Member("bbb", "aaaa", "B", Gender.MALE, LoginType.USER, 5);
        memberRepository.save(member1);
        memberRepository.save(member2);
        for(int i=0; i<20; i++){
            if(i % 2 == 0){ //1번 유저가 리뷰 작성
                makeReview(novel, member1,i);
            }else{
                makeReview(novel, member2,i );
            }
        }
        em.flush();
        em.clear();
    }

    /**
     * 이슈 : 소설에 대응되는 리뷰는 fetch 조인이 가능하나, 리뷰에 대한 페이징은 불가능하다.
     *  --> 소설을 조회하고, 소설에 대한 리뷰를 페이징 하여 별도의 상세 소설 정보 DTo에 정보들을 담아서 반환하는 걸로하자.
     *  --> 위의 쿼리는 2번으로 해결이 가능함. novel 조회 -> review 조회 (author와 member는 fetch 조인으로 한번에 가져올 수 잇음.)
     *  더 좋은 방법이 있는지 고민해보자
     *
     */
    @Test
    void 상세조회테스트(){
        //given : BeforeEach 메소드 실행

        //when : 콩쥐 팥쥐에 대한 정보를 가져온다.
        List<Novel> novels = novelRepository.findByTitleName("콩쥐 팥쥐");
        System.out.println(novels.size());
        Novel kp = novels.get(0);
        log.info("kp review size = {}", kp.getReview_cnt());

        Pageable pageable = PageRequest.of(1, 10); //처음 10개를 가져옴
        Page<Review> result = reviewRepository.findByReview(kp, pageable);
        List<Review> reviews = result.getContent();
        log.info("리뷰 사이즈 : {}", result.getTotalElements());
        for (Review reivew : reviews) {
            log.info("리뷰 작성자 : {}, 부여 평점 : {}, 리뷰 내용 : {}", reivew.getMember().getNickname(), reivew.getRating(), reivew.getContent());
        }

        assertThat(kp.getTitle()).isEqualTo("콩쥐 팥쥐");
        assertThat(reviews.size()).isEqualTo(10);
        assertThat(kp.averageRating()).isEqualTo(5.0);
    }


}
