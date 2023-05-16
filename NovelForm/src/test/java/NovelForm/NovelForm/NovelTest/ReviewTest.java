package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class ReviewTest {


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
    void 리뷰추가테스트(){
        //given : beforeEach 수행
        //given : beforeEach 수행 ssu -> 성냥팔이,홍길동전 리뷰 / test -> 성냥팔이소녀 리뷰 작성완료
        Member findMember2 = memberRepository.findByNickname("test");
        //Pathvariable로 소설 번호가 들어온다고 가정한다.
        Member TEST = memberRepository.findByMemberIdWithReviews(findMember2.getId());

        //when : test 유저가 홍길동전 리뷰 작성
        Novel novel = novelRepository.findByTitleName("홍길동전").get(0);

        ReviewBodyDto input = new ReviewBodyDto(5.0, "아주아주 재밌습니다.");

        Review review = Review.builder()
                .content(input.getContent())
                .rating(input.getRating()).build();
        review.addMember(TEST);
        review.addNovel(novel);

        reviewRepository.save(review);

        em.flush();
        em.clear();

        //then : 홍길동전 소설의 review_cnt가 2개인지 확인한다 기존에 1개였음.
        Novel findNovel = novelRepository.findByNovelIdWithReviews(novel.getId());
        List<Review> r = findNovel.getReviews();
        Member findMember = memberRepository.findByMemberIdWithReviews(TEST.getId());


        //소설 객체에 리뷰가 잘 추가됐는지 확인한다.
        assertThat(findNovel.getReview_cnt()).isEqualTo(2);
        assertThat(findNovel.averageRating()).isEqualTo(4.5); // (5.0+4.0)/2 = 4.5

        //SSU 멤버 객체에 리뷰 개수가 2개인지 확인한다
        assertThat(findMember.getReviews().size()).isEqualTo(2);

    }


    @Test
    void 리뷰삭제테스트(){
        //given : beforeEach 수행 ssu -> 성냥팔이,홍길동전 리뷰 / test -> 홍길동전 리뷰 작성완료
        Member findMember1 = memberRepository.findByNickname("ssu");
        //세션에 회원 번호가 들어온다고 가정한다.
        Member SSU = memberRepository.findByMemberIdWithReviews(findMember1.getId());

        Member findMember2 = memberRepository.findByNickname("test");
        //Pathvariable로 소설 번호가 들어온다고 가정한다. 
        Member TEST = memberRepository.findByMemberIdWithReviews(findMember2.getId());

        List<Novel> list = novelRepository.findByTitleName("성냥팔이 소녀");
        Novel novel = novelRepository.findByNovelIdWithReviews(list.get(0).getId());

        //when : SSU 유저가 성냥팔이 소녀에 대한 리뷰를 삭제한다.
        Review SSUReview = reviewRepository.findSingleReivew(SSU, novel).get();
        //log.info("SSU 리뷰 삭제전 리뷰 수 : {}", SSU.getReviews().size());
        //log.info("성냥팔이 소녀 리뷰 삭제 전 리뷰 수  : {}", novel.getReviews().size());


        SSU.deleteMyReview(SSUReview);
        novel.deleteReview(SSUReview);
//        log.info("SSUReview id = {}", SSUReview.getId());
//        log.info("SSU 리뷰 삭제후 리뷰 수 : {}", SSU.getReviews().size());
//        log.info("novel 리뷰 삭제후 리뷰 수 : {}", novel.getReviews().size());
        reviewRepository.deleteReviewById(SSUReview.getId());

        em.clear();
        em.flush();

        //SSU 회원과 성냥팔이 소설 다시 조회해온다.
        Member findSSU = memberRepository.findByMemberIdWithReviews(findMember1.getId());
        Novel findNovel = novelRepository.findByNovelIdWithReviews(list.get(0).getId());

//        log.info("이름 : {}", findSSU.getNickname());
        for(Review r : findSSU.getReviews()){
//            log.info("리뷰 번호 : {}", r.getId());
        }

//        log.info("소설 이름 : {}", findNovel.getTitle());
        for(Review r : findNovel.getReviews()){
//            log.info("리뷰 번호 : {}", r.getId());
        }

        //then : ssu 유저는 성냥팔이 리뷰를 삭제했으므로 ssu의 리뷰 수는 1개, 성냥팔이 소녀의 리뷰 수는 1개가 되어야한다.
        assertThat(findSSU.getReviews().size()).isEqualTo(1); // 2개에서 하나 삭제했으므로 1개가 되어야함
        assertThat(findNovel.getReview_cnt()).isEqualTo(1); //성냥팔이 소녀는 리뷰를 2개 가지고 있는데 하나 삭제했으므로 1개여야 함.
        assertThat(findNovel.getRating()).isEqualTo(3.5); 
    }

    @Test
    void 리뷰수정테스트(){
        //given : test 유저가 자신의 성냥팔이 소설 리뷰를 수정한다고 하자. --> 세션의 값으로 들어왔다고 가정
        Member findMember = memberRepository.findByNickname("test");
        Member TEST = memberRepository.findById(findMember.getId()).get();
        
        //성냥팔이 소설을 가져온다
        List<Novel> novels = novelRepository.findByTitleName("성냥팔이");
        Novel novel = novels.get(0);
        
        //TEST유저가 작성한 리뷰를 가져온다
        Review myReview = reviewRepository.findSingleReivew(TEST, novel).get();

        // when 평점을 3.5 -> 2.0, 내용을 재미없어요로 수정한다고 가정
        ReviewBodyDto reviewBodyDto = new ReviewBodyDto(2.0, "재미없어요");
        myReview.modifyRating(myReview.getRating(), reviewBodyDto.getRating(), novel);
        myReview.modifyContent(reviewBodyDto.getContent());

        em.flush();
        em.clear();

        //성냥 팔이 소설을 가져온다, 리뷰와 함께,
        List<Review> reviews = reviewRepository.findByReview(novel);
        Novel findNovel = novelRepository.findById(novel.getId()).get();

        for (Review review : reviews) {
//            log.info("리뷰 평점 : {}, 리뷰 내용 : {}", review.getRating(), review.getContent());
        }

        /**
         * NovelRepository에서 리뷰를 가져와서 해당 리뷰 List를
         * 조회시 멤버까지 긁어오는 N+1 문제 발생
         * 해결점 찾아야함.
         * 리뷰를 가져올 때는 review Repository에서 별도로 가져와 넣어줘야할까?
         */

        //then : 평균 평점이 (4.5+2.0)/2 = 3.25
        assertThat(findNovel.averageRating()).isEqualTo(3.25);
    }

}
