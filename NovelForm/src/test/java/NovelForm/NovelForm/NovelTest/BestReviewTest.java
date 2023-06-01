package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.BestReviewDto;
import NovelForm.NovelForm.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class BestReviewTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private LikeRepository likeRepository;

    @BeforeEach
    void createTestSample(){
        Member member1 = Member.builder()
                .email("test@naver.com")
                .password("1234")
                .nickname("ssu")
                .gender(Gender.MALE)
                .loginType(LoginType.USER)
                .build();

        memberRepository.save(member1);

        Member member2 = Member.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickname("test")
                .gender(Gender.MALE)
                .loginType(LoginType.USER)
                .build();

        memberRepository.save(member2);

        Member member3 = Member.builder()
                .email("asdf@naver.com")
                .password("1234")
                .nickname("qqqq")
                .gender(Gender.MALE)
                .loginType(LoginType.USER).build();

        memberRepository.save(member3);

        Member member4 = Member.builder()
                .email("asdfff@naver.com")
                .password("1234")
                .nickname("qwewqe")
                .gender(Gender.MALE)
                .loginType(LoginType.USER).build();

        memberRepository.save(member4);

        Author author = Author.builder()
                .name("숭숭숭")
                .build();
        authorRepository.save(author);

        Author author2 = Author.builder()
                .name("작가작가")
                .build();
        authorRepository.save(author2);

        Novel novel1 = Novel.builder()
                .title("홍길동전")
                .cover_image("asdfasdf")
                .download_cnt(0)
                .category("로맨스")
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
                .category("로맨스판타지")
                .summary("테스트 입력")
                .build();
        novel2.addAuthor(author);

        Novel novel3 = Novel.builder()
                .title("판타지판타지이이")
                .cover_image("testtest")
                .download_cnt(0)
                .category("판타지")
                .rating(0.0)
                .review_cnt(0)
                .summary("테스트 입력")
                .build();
        novel3.addAuthor(author2);

        Novel novel4 = Novel.builder()
                .title("판타지판타지이이")
                .cover_image("testtest")
                .download_cnt(0)
                .category("로맨스판타지")
                .rating(0.0)
                .review_cnt(0)
                .summary("테스트 입력")
                .build();
        novel4.addAuthor(author2);


        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);

        Review review1 = Review.builder()
                .content("재미있어요")
                .rating(4.0)
                .build();
        review1.addMember(member1);
        review1.addNovel(novel1);

        Review review2 = Review.builder()
                .content("박진감 넘쳐요")
                .rating(4.5)
                .build();
        review2.addNovel(novel1);
        review2.addMember(member2);

        Review review3 = Review.builder()
                .content("꿀잼")
                .rating(3.5)
                .build();
        review3.addNovel(novel1);
        review3.addMember(member3);

        Review review7 = Review.builder()
                .content("test")
                .rating(2.0)
                .build();
        review7.addNovel(novel1);
        review7.addMember(member4);

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review7);

        Review review4 = Review.builder()
                .content("꿀잼")
                .rating(1.0)
                .build();
        review4.addNovel(novel2);
        review4.addMember(member1);

        Review review5 = Review.builder()
                .content("꿀잼")
                .rating(1.0)
                .build();
        review5.addNovel(novel2);
        review5.addMember(member2);
        Review review6 = Review.builder()
                .content("꿀잼")
                .rating(1.0)
                .build();
        review6.addNovel(novel2);
        review6.addMember(member3);

        Review review8 = Review.builder()
                .content("꿀잼")
                .rating(5.0)
                .build();
        review8.addNovel(novel3);
        review8.addMember(member4);

        Review review9 = Review.builder()
                .content("빅잼")
                .rating(3.0)
                .build();
        review9.addNovel(novel4);
        review9.addMember(member2);

        reviewRepository.save(review4);
        reviewRepository.save(review5);
        reviewRepository.save(review6);
        reviewRepository.save(review8);
        reviewRepository.save(review9);


        Like like1 = new Like(member1, review1);
        Like like13 = new Like(member4, review1);
        Like like2 = new Like(member2, review1);
        Like like3 = new Like(member3, review1);
        Like like4 = new Like(member1, review2);
        Like like5 = new Like(member2, review2);
        Like like6 = new Like(member3, review3);

        Like like7 = new Like(member1, review4);
        Like like8 = new Like(member2, review4);
        Like like9 = new Like(member3, review4);
        Like like10 = new Like(member1, review5);
        Like like11 = new Like(member2, review5);
        Like like12 = new Like(member3, review6);

        Like like14 = new Like(member1, review8);
        Like like15 = new Like(member2, review8);
        Like like16 = new Like(member3, review8);
        Like like17 = new Like(member4, review8);

        Like like18 = new Like(member1, review9);
        Like like19 = new Like(member3, review9);

        likeRepository.save(like1);
        likeRepository.save(like2);
        likeRepository.save(like3);
        likeRepository.save(like4);
        likeRepository.save(like5);
        likeRepository.save(like6);
        likeRepository.save(like7);
        likeRepository.save(like8);
        likeRepository.save(like9);
        likeRepository.save(like10);
        likeRepository.save(like11);
        likeRepository.save(like12);
        likeRepository.save(like13);
        likeRepository.save(like14);
        likeRepository.save(like15);
        likeRepository.save(like16);
        likeRepository.save(like17);
        likeRepository.save(like18);
        likeRepository.save(like19);

        em.flush();
        em.clear();
    }

    @Test
    void 베스트리뷰테스트(){
        //장르 : 판타지
        String genre = "로맨스";
        String title1 = "홍길";
        //페이지 : 0페이지
        Pageable pageable = PageRequest.of(0, 10);

        Page<BestReviewDto> result = likeRepository.findNovelWithinGenreLikeReviewDescForRomance(title1, genre, pageable);
        List<BestReviewDto> r1 = result.getContent();

        for (BestReviewDto bestReviewDto : r1) {
            log.info("nove_id = {}, novel = {}, name = {}, rating = {}, \n" +
                    "image = {}, content = {}, nickname = {}, like = {}",
                    bestReviewDto.getId(),
                    bestReviewDto.getTitle(),bestReviewDto.getName(),bestReviewDto.getRating(),
                    bestReviewDto.getImage_url(), bestReviewDto.getContent(), bestReviewDto.getNickName(),
                    bestReviewDto.getLike_count());
        }

        em.flush();
        em.clear();

        String title2 = "";
        String genre2 = "로맨스판타지";
        Page<BestReviewDto> result2 = likeRepository.findNovelWithinGenreLikeReviewDesc(title2, genre2,  pageable);
        List<BestReviewDto> r2 = result2.getContent();
        log.info("total ele = {}", result2.getTotalElements());
        for (BestReviewDto bestReviewDto : r2) {
            log.info("nove_id = {}, novel = {}, name = {}, rating = {}, \n" +
                            "image = {}, content = {}, nickname = {}, like = {}",
                    bestReviewDto.getId(),
                    bestReviewDto.getTitle(),bestReviewDto.getName(),bestReviewDto.getRating(),
                    bestReviewDto.getImage_url(), bestReviewDto.getContent(), bestReviewDto.getNickName(),
                    bestReviewDto.getLike_count());
        }

        /**
         * 전 장르 베스트 리뷰를 가져옴
         */
        Page<BestReviewDto> result3 = likeRepository.findNovelWithinGenreLikeReviewDesc("", "", pageable);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(result2.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(result3.getTotalElements()).isEqualTo(8);
    }


}
