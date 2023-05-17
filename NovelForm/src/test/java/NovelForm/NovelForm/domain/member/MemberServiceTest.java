package NovelForm.NovelForm.domain.member;


import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.UpdateMemberRequest;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.repository.*;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoxRepository boxRepository;

    @Autowired
    FavoriteBoxRepository favoriteBoxRepository;

    @Autowired
    FavoriteNovelRepository favoriteNovelRepository;

    @Autowired
    FavoriteAuthorRepository favoriteAuthorRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    CommunityPostRepository communityPostRepository;

    @Autowired
    EntityManager em;


    @BeforeEach
    void setMember(){

        Member member1 = new Member(
                "test0101@test.com",
                "sdsdsdsdsd",
                "testnick_1",
                Gender.MALE,
                LoginType.USER,
                38
        );

        Member member2 = new Member(
                "testDel@test.com",
                "sdsdsdsdsd",
                "testnick_del",
                Gender.MALE,
                LoginType.USER,
                15
        );

        memberRepository.save(member1);
        memberRepository.save(member2);


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
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );
        novelRepository.save(testNovel1);

        Review review = new Review(
                "activated",
                testNovel1,
                member2,
                3.5,
                "test 내용입니다."
        );
        reviewRepository.save(review);


        BoxItem boxItem = new BoxItem(1,testNovel1);


        Box box = new Box(
                "test title",
                "test content",
                0,
                member2
        );

        box.addBoxItem(boxItem);
        boxRepository.save(box);


        FavoriteAuthor favoriteAuthor = new FavoriteAuthor(testAuthor1);
        favoriteAuthorRepository.save(favoriteAuthor);

        FavoriteBox favoriteBox = new FavoriteBox(box);
        favoriteBoxRepository.save(favoriteBox);

        FavoriteNovel favoriteNovel = new FavoriteNovel(testNovel1);
        favoriteNovelRepository.save(favoriteNovel);


        Like like = new Like(member2, box);
        likeRepository.save(like);


        CommunityPost communityPost = new CommunityPost("test titel", "testConde", member2);
        communityPostRepository.save(communityPost);


    }



    @Test
    @DisplayName("회원 수정 테스트 - 같은 닉네임 체크, 정상동작 체크")
    void updateMember() throws MemberDuplicateException {

        // given
        Member member = memberRepository.findByNickname("testnick_1");


        // 실패용 데이터 - 같은 닉네임일 때 체크
        UpdateMemberRequest updateMemberRequestFail = new UpdateMemberRequest(
                "asdasdasdad",
                "testnick_1",
                Gender.MALE,
                "2011-01-01"
        );

        UpdateMemberRequest updateMemberRequestSuccess = new UpdateMemberRequest(
                "password123",
                "qwerasdf",
                Gender.FEMALE,
                "2000-01-01"
        );



        // when
        // 같은 닉네임이면 예외 발생
        Assertions.assertThrows(MemberDuplicateException.class,
                ()->{
                    if (memberRepository.findByNickname(updateMemberRequestFail.getNickname()) != null){
                        Map<String, String> errorFieldMap = new HashMap<>();
                        errorFieldMap.put("nickname", updateMemberRequestFail.getNickname());
                        throw new MemberDuplicateException(errorFieldMap);
                    }
                });


        LocalDate birth = LocalDate.parse(updateMemberRequestSuccess.getAge());
        LocalDate nowDate = LocalDate.now();

        Integer age = (int) ChronoUnit.YEARS.between(birth, nowDate);


        if (memberRepository.findByNickname(updateMemberRequestSuccess.getNickname()) != null){
            Map<String, String> errorFieldMap = new HashMap<>();
            errorFieldMap.put("nickname", updateMemberRequestSuccess.getNickname());
            throw new MemberDuplicateException(errorFieldMap);
        }


        member.updateMember(
                updateMemberRequestSuccess.getPassword(),
                updateMemberRequestSuccess.getNickname(),
                updateMemberRequestSuccess.getGender(),
                age
        );


        em.flush();
        em.clear();

        // 수정이 정상적으로 진행 됐는가?
        member = memberRepository.findByNickname(updateMemberRequestSuccess.getNickname());

        org.assertj.core.api.Assertions.assertThat(member.getAge()).isEqualTo(age);
        org.assertj.core.api.Assertions.assertThat(member.getGender()).isEqualTo(updateMemberRequestSuccess.getGender());

    }


    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteMember(){

        // given
        // 멤버 하나를 가져와서
        Member member = memberRepository.findByNickname("testnick_del");
        Long memberId = member.getId();

        // when
        // 삭제한다.
        // 다만, 멤버 하나 하고만 연결이 되어 있는 경우에는 바로 Cascade를 이용해서 삭제하지만,
        // 그 외의 경우에는 직접 삭제한다.

        List<Box> delBoxs = boxRepository.findBoxesByMemberId(memberId);

        // H2 Database에 on delete cascade 옵션이 BoxItem에 없어 테스트에서는 우선 BoxItem을 삭제 해야 한다.
        // orphanRemoval = true 로 설정되어 있기 때문에, 부모 객체에서 떨어져 나오면, 자동으로 삭제된다.
        // 때문에 부모와의 관계를 끊어주는 작업이 필요하고, 이 작업으로 인한 변경사항이 DB에 바로 적용되도록 flush를 했다.
        // bulk 쿼리는 DB에 직접 쿼리를 날리기 때문에 flush 전에 동작할 수 있기 떄문이다.
        for (Box delBox : delBoxs) {
            delBox.getBoxItems().clear();
        }
        em.flush();

        // 보관함 삭제
        boxRepository.bulkDeleteBoxByMember(member);

        // 리뷰 삭제
        reviewRepository.bulkDeleteReviewByMember(member);

        // 커뮤니티 글 삭제
        communityPostRepository.deleteAllByMember(member);

        //em.flush();


        memberRepository.delete(member);

        em.flush();
        em.clear();

        // then
        // 삭제한 회원을 찾을 수 없어야 함.
        org.assertj.core.api.Assertions.assertThat(memberRepository.findByNickname("testnick_del")).isNull();

        // 또한, 삭제한 회원이 작성한 모든게 같이 사라져야 함.
        org.assertj.core.api.Assertions.assertThat(boxRepository.findBoxesByMemberId(memberId)).isEmpty();
        org.assertj.core.api.Assertions.assertThat(favoriteAuthorRepository.findFavoriteAuthorsByMemberId(memberId)).isEmpty();

        //Assertions.


    }


}
