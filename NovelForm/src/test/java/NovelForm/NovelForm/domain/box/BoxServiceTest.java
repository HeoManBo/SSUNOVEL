package NovelForm.NovelForm.domain.box;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.CreateBoxRequest;
import NovelForm.NovelForm.domain.box.exception.NoSuchBoxItemException;
import NovelForm.NovelForm.domain.box.exception.WrongBoxException;
import NovelForm.NovelForm.domain.box.exception.WrongMemberException;
import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.repository.*;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
@Transactional
public class BoxServiceTest {


    @Autowired
    BoxRepository boxRepository;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FavoriteBoxRepository favoriteBoxRepository;

    @Autowired
    EntityManager em;


    @BeforeEach
    void setBox(){
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

            Review review = new Review(
                    "activated",
                    novelList.get(i),
                    memberList.get(i),
                    3.5,
                    "test review" + i
            );
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
                    0,
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





        // 즐겨찾기 보관함 등록
        // 역순으로 보관함 즐겨찾기
        List<FavoriteBox> favoriteBoxList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            FavoriteBox favoriteBox = new FavoriteBox(boxList.get(9 - i));
            favoriteBox.addMember(memberList.get(i));

            favoriteBoxList.add(favoriteBox);
        }

        favoriteBoxRepository.saveAll(favoriteBoxList);

        em.flush();
        em.clear();
    }



    @Test
    @DisplayName("보관함 수정 테스트")
    void updateBox() throws WrongMemberException, WrongBoxException {

        // given
        Member member = new Member("test@test.com", "asdasdasd", "cick", Gender.FEMALE, LoginType.USER,23);

        Author author = new Author("test Author");

        Novel novel = new Novel(
                "title",
                "summary",
                100,
                100,
                220,
                "연재중",
                "https://1211212.csr",
                3.8,
                29,
                "판타지",
                author,
                "sdsdsdsds",
                "kakakakak",
                "ririririri",
                "mmumumum"
        );

        Novel novel2 = new Novel(
                "title",
                "summary",
                100,
                100,
                220,
                "연재중",
                "https://1211212.csr",
                3.8,
                29,
                "판타지",
                author,
                "sdsdsdsds",
                "kakakakak",
                "ririririri",
                "mmumumum"
        );

        BoxItem boxItem = new BoxItem(1, novel);

        Box box = new Box("title", "content", 0, member);


        Long requestMemberId = memberRepository.save(member).getId();
        authorRepository.save(author);
        novelRepository.save(novel);
        Long requestBoxId = boxRepository.save(box).getId();

        Long testNovelId = novelRepository.save(novel2).getId();



        em.flush();
        em.clear();


        List<Long> updateList = new ArrayList<>();
        updateList.add(testNovelId);

        CreateBoxRequest createBoxRequest = new CreateBoxRequest(
                "update title",
                "update content",
                0,
                updateList,
                testNovelId
        );


        // when
        // 보관함 업데이트
        Optional<Member> optionalMember = memberRepository.findById(requestBoxId);
        Optional<Box> optionalBox = boxRepository.findById(requestBoxId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException();
        }

        if(!optionalBox.isPresent()){
            throw new WrongBoxException();
        }

        Member findMember = optionalMember.get();
        Box findBox = optionalBox.get();


        List<BoxItem> boxItemList = new ArrayList<>();

        // 보관함 아이템
        for (Long boxItemId : createBoxRequest.getBoxItems()) {

            Optional<Novel> optionalNovel = novelRepository.findById(boxItemId);
            Novel findNovel;



            // 없는 작품을 보관함에 넣으려고 하면, 실패
            if(optionalNovel.isPresent()){
                findNovel = novelRepository.findById(boxItemId).get();
            }
            else{
                throw new NoSuchBoxItemException("없는소설번호: " + boxItemId);
            }


            BoxItem updateBoxItem;

            if (boxItemId == createBoxRequest.getLeadItemId()){
                updateBoxItem = new BoxItem(1, findNovel);
            }
            else{
                updateBoxItem = new BoxItem(0, findNovel);
            }

            boxItemList.add(updateBoxItem);
        }

        findBox.updateBox(
                createBoxRequest.getTitle(),
                createBoxRequest.getContent(),
                createBoxRequest.getIs_private(),
                boxItemList
        );


        em.flush();
        em.clear();

        // then
        // 변경사항이 반영되었는지 확인

        Box updateBox = boxRepository.findById(requestBoxId).get();

        Assertions.assertThat(updateBox.getTitle()).isEqualTo(createBoxRequest.getTitle());
        Assertions.assertThat(updateBox.getContent()).isEqualTo(createBoxRequest.getContent());
        Assertions.assertThat(updateBox.getIs_private()).isEqualTo(createBoxRequest.getIs_private());

        for (BoxItem item : updateBox.getBoxItems()) {
            Assertions.assertThat(createBoxRequest.getBoxItems()).contains(item.getNovel().getId());
        }



    }



    @Test
    @DisplayName("보관함 검색 테스트")
    void searchBox(){

    }

}
