package NovelForm.NovelForm.domain.member;


import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.*;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.repository.*;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@Transactional
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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

        List<Member> memberList = new ArrayList<>();

        // 회원 10명 생성
        for(int i = 0; i < 10; i++){
            Member member = new Member(
                    "test" + i + "@test.com",
                    "sdsdsdsdsd",
                    "testnick_" + i,
                    Gender.MALE,
                    LoginType.USER,
                    38
            );
            memberList.add(member);
        }
        memberRepository.saveAll(memberList);


        // 작가 10명 및 각 작가당 소설 1개씩 생성
        List<Author> authorList = new ArrayList<>();
        List<Novel> novelList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            Author testAuthor = new Author("test Author" + i);
            authorList.add(testAuthor);

            Novel testNovel = new Novel(
                    "test title" + i,
                    "test summary" + i,
                    121,
                    100,
                    390,
                    "연재중",
                    "https://img/src/" + i,
                    3.8,
                    198,
                    "판타지",
                    testAuthor,
                    "navernaver",
                    "kakaokakao",
                    "ridiridi",
                    "munpiamunpia"
            );

            novelList.add(testNovel);
        }
        authorRepository.saveAll(authorList);
        novelRepository.saveAll(novelList);


        log.info("size = {}", novelList.size());

        // 모든 회원이(10명) 하나씩은 리뷰를 작성
        List<Review> reviewList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            System.out.println(i);

            Review review = Review.builder()
                    .rating(3.5)
                    .content("test content")
                    .build();

            review.addNovel(novelList.get(i));
            review.addMember(memberList.get(i));
            reviewList.add(review);
        }

        reviewRepository.saveAll(reviewList);



        // 보관함
        // 각 보관함마다 보관함 아이템 5개씩 생성
        // 모든 보관함은 비공개로 설정
        List<Box> boxList = new ArrayList<>();

        for(int i = 0; i < 10; i++){

            List<BoxItem> boxItemList = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                BoxItem boxItem = new BoxItem(novelList.get(j).getId().intValue(), novelList.get(j));
               boxItemList.add(boxItem);
            }

            Box box = new Box(
                    "test title",
                    "test content",
                    1,
                    memberList.get(i)
            );
            for (BoxItem boxItem : boxItemList) {
                box.addBoxItem(boxItem);
            }
            boxList.add(box);
        }

        boxRepository.saveAll(boxList);



        // 좋아요 설정
        // 모든 회윈이 각자의 보관함에는 좋아요를 등록
        List<Like> likeList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            Like like = new Like(memberList.get(i), boxList.get(i));
            likeList.add(like);
        }
        likeRepository.saveAll(likeList);



        // 커뮤니티에 글 쓰기
        // 모든 회원이 각각 5개씩 쓴다.
        for(int i = 0; i < 10; i++){
            List<CommunityPost> communityPostList = new ArrayList<>();

            for(int j = 0; j < 5; j++){
                CommunityPost communityPost = new CommunityPost("test tite" + i + "의" + j, "test content" + i + "의" + j, memberList.get(i));
                communityPostList.add(communityPost);
            }

            communityPostRepository.saveAll(communityPostList);
        }

        // 즐겨찾기 보관함 등록
        // 역순으로 보관함 즐겨찾기
        List<FavoriteBox> favoriteBoxList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            FavoriteBox favoriteBox = new FavoriteBox(boxList.get(9 - i));
            favoriteBox.addMember(memberList.get(i));

            favoriteBoxList.add(favoriteBox);
        }

        favoriteBoxRepository.saveAll(favoriteBoxList);


        // 즐겨찾기 작가 등록
        // 각자 자기 번호에 맞는 작가 즐겨찾기
        List<FavoriteAuthor> favoriteAuthorList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            FavoriteAuthor favoriteAuthor = new FavoriteAuthor(authorList.get(i));
            favoriteAuthor.addMember(memberList.get(i));

            favoriteAuthorList.add(favoriteAuthor);
        }

        favoriteAuthorRepository.saveAll(favoriteAuthorList);


        // 즐겨찾기 소설 등록
        // 자기 번호에 맞는 소설 즐겨찾기
        List<FavoriteNovel> favoriteNovelList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            FavoriteNovel favoriteNovel = new FavoriteNovel(novelList.get(i));
            favoriteNovel.addMember(memberList.get(i));

            favoriteNovelList.add(favoriteNovel);
        }

        favoriteNovelRepository.saveAll(favoriteNovelList);

        em.flush();
        em.clear();
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
        Member givenMember = memberRepository.save(new Member("testdel@naver.com",
                "123123123",
                "testnick_del",
                Gender.MALE,
                LoginType.USER,
                40));

        em.flush();
        em.clear();


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

        // 보관함 즐겨찾기 삭제
        for (Box delBox : delBoxs) {
            favoriteBoxRepository.deleteAllByBox(delBox);
        }

        // 보관함 좋아요 삭제
        for (Box delBox : delBoxs){
            likeRepository.deleteAllByBox(delBox);
        }


        favoriteBoxRepository.deleteAllByMember(member);



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

    }




    /**
     * 마이페이지
     *
     * 내가 작성하거나 즐겨찾기에 등록한 것들을 가져오기
     * 작성 글
     * 작성 리뷰
     * 생성한 보관함
     * 즐겨찾기한 작가 목록
     * 즐겨찾기한 보관함 목록
     * 즐겨찾기한 소설 목록
     */
    @Test
    @DisplayName("마이페이지 서비스 테스트")
    void getMyPage(){

        //given
        //setMember에서 설정한 내역을 바탕으로 작업
        List<Member> memberList = memberRepository.findAll();
        PageRequest pageRequest = PageRequest.of(1, 10, Sort.by("update_at").descending());



        //when
        // 작성글 개수 확인
        for (Member member : memberList) {
            //List<PostDto> memberPostList = communityPostRepository.findPostByMember(member, pageRequest).getContent();

            //MemberPostResponse memberPostResponse = new MemberPostResponse(memberPostList.size(), memberPostList);

            //then
            // 모든 회원이 5개씩 글을 썼으므로 5개를 가져와야 함.
            //org.assertj.core.api.Assertions.assertThat(memberPostResponse.getMemberPostCnt()).isEqualTo(5);
        }


        //when
        // 작성 리뷰 확인

        for (Member member : memberList) {
            List<MemberReviewInfo> memberReviewInfoList = reviewRepository.findMemberReviewByMember(member, pageRequest).getContent();

            MemberReviewResponse memberReviewResponse = new MemberReviewResponse(memberReviewInfoList.size(), memberReviewInfoList);

            // then
            // 모든 회원들은 리뷰를 하나씩은 작성했다.
            org.assertj.core.api.Assertions.assertThat(memberReviewResponse.getReviewCnt()).isEqualTo(1);
        }


        // when
        // 생성한 보관함 확인
        for (Member member : memberList) {
            List<MemberBoxInfo> memberBoxInfoList = boxRepository.findMemberBoxByMember(member, pageRequest).getContent();

            MemberBoxResponse memberBoxResponse = new MemberBoxResponse(memberBoxInfoList.size(), memberBoxInfoList);

            // then
            // 모든 회원은 보관함을 1개씩 작성했다. 보관함내 작품은 5개가 들어가 있다.
            org.assertj.core.api.Assertions.assertThat(memberBoxResponse.getBoxCnt()).isEqualTo(1);
            org.assertj.core.api.Assertions.assertThat(memberBoxInfoList.get(0).getItemCnt()).isEqualTo(5);
        }


        // when
        // 즐겨찾기한 보관함 확인
        for (Member member : memberList) {
            List<MemberBoxInfo> memberFavoriteBoxList = favoriteBoxRepository.findMemberFavoriteBoxByMember(member, pageRequest).getContent();

            MemberFavoriteBoxResponse memberFavoriteBoxResponse = new MemberFavoriteBoxResponse(memberFavoriteBoxList.size(), memberFavoriteBoxList);

            // then
            // 모든 회원은 역순으로 보관함을 하나 씩 즐겨 찾기 해두었다.
            // 좋아요는 모든 보관함이 1개씩 받았다.
            org.assertj.core.api.Assertions.assertThat(memberFavoriteBoxResponse.getFavoriteBoxCnt()).isEqualTo(1);
            org.assertj.core.api.Assertions.assertThat(memberFavoriteBoxResponse.getMemberBoxInfoList().get(0).getLikeCnt())
                    .isEqualTo(1);
        }


        // when
        // 즐겨찾기한 작가 확인
        for(Member member : memberList){

            List<Author> authorList = authorRepository.findAuthorsByMemberFavorite(member, pageRequest).getContent();
            List<MemberFavoriteAuthorInfo> memberFavoriteAuthorInfoList = new ArrayList<>();

            for (Author author : authorList) {
                // Comparator의 람다식
                // 식의 결과가 양수이면 자리를 바꾸고, 음수이면 그대로 유지하게 된다.
                // 뒤에 있는 값이 크면 앞으로 오게 되기에 내림차순 정렬이 가능하다.
                author.getNovels().sort((a, b) -> b.getDownload_cnt() - a.getDownload_cnt());

                Novel mostNovel = author.getNovels().get(0);

                log.info("novel: {}", mostNovel);



                MemberFavoriteAuthorInfo memberFavoriteAuthorInfo = new MemberFavoriteAuthorInfo(
                        author.getId(),
                        author.getName(),
                        mostNovel.getCover_image(),
                        mostNovel.getTitle());

                memberFavoriteAuthorInfoList.add(memberFavoriteAuthorInfo);
            }

            MemberFavoriteAuthorResponse memberFavoriteAuthorResponse =
                    new MemberFavoriteAuthorResponse(memberFavoriteAuthorInfoList.size(), memberFavoriteAuthorInfoList);


            // then
            // 즐겨찾기 작가는 1명씩
            org.assertj.core.api.Assertions.assertThat(memberFavoriteAuthorResponse.getAuthorCnt()).isEqualTo(1);

        }


        // when
        // 즐겨찾기한 소설 확인
        for (Member member : memberList) {

            List<MemberFavoriteNovelInfo> novelInfoList = favoriteNovelRepository.findFavoriteNovelInfoByMember(member, pageRequest).getContent();
            
            MemberFavoriteNovelResponse memberFavoriteNovelResponse = new MemberFavoriteNovelResponse(novelInfoList.size(), novelInfoList);
            
            //then
            // 즐겨찾기 한 소설은 모두 1개씩
            org.assertj.core.api.Assertions.assertThat(memberFavoriteNovelResponse.getNovelCnt()).isEqualTo(1);
        }










    }



}
