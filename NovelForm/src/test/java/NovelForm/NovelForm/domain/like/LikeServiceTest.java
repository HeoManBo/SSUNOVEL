package NovelForm.NovelForm.domain.like;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.like.exception.DuplicateAddLikeException;
import NovelForm.NovelForm.domain.like.exception.EmptyLikeException;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
class LikeServiceTest {



    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    BoxRepository boxRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    NovelRepository novelRepository;
    @Autowired
    AuthorRepository authorRepository;


    @BeforeEach
    void setBox(){
        Author testAuthor1 = new Author("test Author1");
        authorRepository.save(testAuthor1);

        Novel testNovel1 = new Novel(
                "test title1",
                "test summary1",
                121,
                100,
                390,
                "연재중",
                "https://img/src",
                3.8,
                198,
                "판타지",
                testAuthor1,
                "navernaver",
                "kakaokaka",
                "ridiridi",
                "munpiamunpia"
        );

        novelRepository.save(testNovel1);

        Member member1 = new Member(
                "tm1@naver.com",
                "12123838",
                "test",
                Gender.MALE,
                LoginType.USER,
                38
        );

        memberRepository.save(member1);

        BoxItem testItem1 = new BoxItem(1, testNovel1);

        Box testBox1 = new Box("box title 1", "content 1", 0, member1);
        testBox1.addBoxItem(testItem1);

        boxRepository.save(testBox1);


        Review review = new Review(
                "activated",
                testNovel1,
                member1,
                3.5,
                "너무 재맜습니다." +
                        "흑흑"
                );

        reviewRepository.save(review);
    }


    @Test
    @DisplayName("보관함 좋아요 등록/취소 테스트")
    void addLikeToBox() throws DuplicateAddLikeException {

        // given
        // 정상적인 멤버와 보관함이 주어진다
        Member member = memberRepository.findByNickname("test");
        Box box = boxRepository.findAll().get(0);


        // when
        // 해당 멤버가 보관함에 대해 좋아요를 눌렀는지 확인하고 없으면, 좋아요를 저장한다.
        List<Like> likeList = likeRepository.findLikesByMemberAndBox(member, box);


        if(!likeList.isEmpty()){
            Map<String, Long> errorFieldMap = new HashMap<>();
            errorFieldMap.put("memberId", member.getId());
            errorFieldMap.put("boxId", box.getId());

            throw new DuplicateAddLikeException(errorFieldMap);
        }

        Like like = new Like(member, box);

        Like save = likeRepository.save(like);


        // then
        // 좋아요에 등록한 정보가 맞는지 확인
        Assertions.assertThat(save.getMember().getId()).isEqualTo(member.getId());
        Assertions.assertThat(save.getBox().getId()).isEqualTo(box.getId());



        // when + then
        // 같은 정보로 한 번 더 등록을 시도할 경우 예외 발생
        org.junit.jupiter.api.Assertions.assertThrows(
                DuplicateAddLikeException.class,
                ()->{
                    List<Like> likeList2 = likeRepository.findLikesByMemberAndBox(member, box);


                    if(!likeList2.isEmpty()){
                        Map<String, Long> errorFieldMap = new HashMap<>();
                        errorFieldMap.put("memberId", member.getId());
                        errorFieldMap.put("boxId", box.getId());

                        throw new DuplicateAddLikeException(errorFieldMap);
                    }

                    Like like2 = new Like(member, box);

                    Like save2 = likeRepository.save(like);
                }
        );




        // when
        // 등록한 좋아요를 취소
        likeRepository.delete(save);

        // then
        // 취소한 후에는 찾을 수 없어야 한다.
        // 취소한걸 취소하는 건 불가능
        Assertions.assertThat(likeRepository.findLikesByMemberAndBox(member, box)).isEmpty();

        org.junit.jupiter.api.Assertions.assertThrows(
                EmptyLikeException.class,
                () -> {

                    List<Like> likeList2 = likeRepository.findLikesByMemberAndBox(member, box);

                    if(likeList2.isEmpty()){
                        Map<String, Long> errorFieldMap = new HashMap<>();
                        errorFieldMap.put("memberId", member.getId());
                        errorFieldMap.put("boxId", box.getId());

                        throw new EmptyLikeException(errorFieldMap);
                    }

                    likeRepository.delete(save);
                }
        );

    }



    @Test
    @DisplayName("리뷰 좋아요 등록/취소 테스트")
    void addLikeToReview() throws DuplicateAddLikeException {

        // given
        // 정상적인 멤버와 리뷰가 주어진다.
        Member member = memberRepository.findAll().get(0);
        Review review = reviewRepository.findAll().get(0);


        // when
        // 좋아요를 이미 등록했는지 확인하고, 등록하지 않았다면 좋아요 등록
        List<Like> likeList = likeRepository.findLikesByMemberAndReview(member, review);

        if(!likeList.isEmpty()){
            Map<String, Long> errorFieldMap = new HashMap<>();
            errorFieldMap.put("memberId", member.getId());
            errorFieldMap.put("reviewId", review.getId());

            throw new DuplicateAddLikeException(errorFieldMap);
        }


        Like like = new Like(member, review);
        Like save = likeRepository.save(like);


        // then
        // 등록한 좋아요의 정보가 입력한 정보와 맞는지 확인 (member, review)
        Assertions.assertThat(save.getMember().getId()).isEqualTo(member.getId());
        Assertions.assertThat(save.getReview().getId()).isEqualTo(review.getId());



        // 만약 등록한 좋아요에 대해서 한 번더 좋아요 등록시 예외가 나도록 처리
        org.junit.jupiter.api.Assertions.assertThrows(
                DuplicateAddLikeException.class,
                () -> {
                    List<Like> likeList2 = likeRepository.findLikesByMemberAndReview(member, review);

                    if(!likeList2.isEmpty()){
                        Map<String, Long> errorFieldMap = new HashMap<>();
                        errorFieldMap.put("memberId", member.getId());
                        errorFieldMap.put("reviewId", review.getId());

                        throw new DuplicateAddLikeException(errorFieldMap);
                    }
                }
        );


        // when
        // 등록한 좋아요 취소
        likeRepository.delete(save);

        // then
        // 취소한 좋아요는 찾을 수 없어야 한다.
        Assertions.assertThat(likeRepository.findLikesByMemberAndReview(member, review)).isEmpty();

        org.junit.jupiter.api.Assertions.assertThrows(
                EmptyLikeException.class,
                () -> {

                    List<Like> likeList2 = likeRepository.findLikesByMemberAndReview(member, review);

                    if(likeList2.isEmpty()){
                        Map<String, Long> errorFieldMap = new HashMap<>();
                        errorFieldMap.put("memberId", member.getId());
                        errorFieldMap.put("reviewId", review.getId());

                        throw new EmptyLikeException(errorFieldMap);
                    }

                    likeRepository.delete(save);
                }
        );

    }



    @Test
    @DisplayName("리뷰 별 좋아요 수 체크 테스트")
    void checkLikeCntByReview(){


        // given
        // 리뷰, 사용자, 리뷰별 좋아요 개수가 있다.
        Member member = new Member("testtt99@gmail.com","12121212", "testtn", Gender.MALE, LoginType.USER, 31);
        memberRepository.save(member);

        Review review = reviewRepository.findAll().get(0);

        Integer reviewCnt = likeRepository.findLikeCountByReview(review);

        // when
        // 리뷰를 하나 추가하면,
        Like like = new Like(member, review);
        likeRepository.save(like);

        // then
        // 추가분이 반영된다.
        Assertions.assertThat(likeRepository.findLikeCountByReview(review)).isEqualTo(reviewCnt + 1);
    }
}