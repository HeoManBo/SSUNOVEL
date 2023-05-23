package NovelForm.NovelForm.domain.Favorite;


import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.favorite.exception.DuplicateFavoriteException;
import NovelForm.NovelForm.domain.favorite.exception.WrongFavoriteAccessException;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class FavoriteServiceTest {


    @Autowired
    FavoriteNovelRepository favoriteNovelRepository;

    @Autowired
    FavoriteAuthorRepository favoriteAuthorRepository;

    @Autowired
    FavoriteBoxRepository favoriteBoxRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoxRepository boxRepository;


    /**
     * DB에 테스트용 데이트 추가 부분
     */
    @BeforeEach
    void setBox(){

        Author testAuthor1 = new Author("test Author1");
        Author testAuthor2 = new Author("test Author2");
        Author testAuthor3 = new Author("test Author3");

        authorRepository.save(testAuthor1);
        authorRepository.save(testAuthor2);
        authorRepository.save(testAuthor3);

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

        Novel testNovel2 = new Novel(
                "test title2",
                "test summary2",
                1211,
                100,
                3901,
                "연재중",
                "https://img/src22222",
                3.8,
                1981,
                "판타지",
                testAuthor2,
                "navernaver",
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );

        Novel testNovel3 = new Novel(
                "test title3",
                "test summary333",
                1213,
                100,
                39033,
                "연재중",
                "https://img/src333",
                9.8,
                198,
                "판타지",
                testAuthor3,
                "navernaver",
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );

        Novel testNovel4 = new Novel(
                "test title4",
                "test summary4",
                12124,
                100,
                39024,
                "연재중",
                "https://img/src444",
                3.8,
                198,
                "판타지",
                testAuthor1,
                "navernaver",
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );

        Novel testNovel5 = new Novel(
                "test title5",
                "test summary555",
                121,
                100,
                390,
                "연재중",
                "https://img/src555",
                3.8,
                198,
                "판타지",
                testAuthor2,
                "navernaver",
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );

        Novel testNovel6 = new Novel(
                "test title6",
                "test summary666",
                1216,
                100,
                3906,
                "연재중",
                "https://img/src666",
                3.8,
                1986,
                "판타지",
                testAuthor3,
                "navernaver",
                "kakaokakao",
                "ridiridi",
                "munpiamunpia"
        );


        novelRepository.save(testNovel1);
        novelRepository.save(testNovel2);
        novelRepository.save(testNovel3);
        novelRepository.save(testNovel4);
        novelRepository.save(testNovel5);
        novelRepository.save(testNovel6);


        Member member1 = new Member(
                "tm1@naver.com",
                "12123838",
                "tmName11",
                Gender.MALE,
                LoginType.USER,
                38
        );

        Member member2 = new Member(
                "tm122@naver.com",
                "12123838",
                "tmName22",
                Gender.MALE,
                LoginType.USER,
                38
        );

        Member member3 = new Member(
                "tm133@naver.com",
                "12123838",
                "tmName33",
                Gender.MALE,
                LoginType.USER,
                38
        );


        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);


        BoxItem testItem1 = new BoxItem(1, testNovel1);
        BoxItem testItem2 = new BoxItem(0, testNovel2);
        BoxItem testItem3 = new BoxItem(0, testNovel3);



        Box testBox1 = new Box("box title 1", "content 1", 0, member1);
        testBox1.addBoxItem(testItem1);
        testBox1.addBoxItem(testItem2);
        testBox1.addBoxItem(testItem3);
        boxRepository.save(testBox1);
    }


    /**
     *  즐겨찾기 추가 테스트
     */
    @DisplayName("즐겨찾기 추가 테스트 (성공 테스트 - 소설, 보관함, 작가)")
    @Test
    void addFavorite(){

        // given
        List<Novel> novels = novelRepository.findAll();
        List<Author> authors = authorRepository.findAll();
        List<Box> boxes = boxRepository.findAll();
        List<Member> members = memberRepository.findAll();



        // 각 사용자에게 소설 즐겨찾기를 등록
        for (Novel novel : novels) {


            for (Member member : members) {
                Novel findNovel = novelRepository.findById(novel.getId()).get();
                Member findMember = memberRepository.findById(member.getId()).get();


                FavoriteNovel favoriteNovel = new FavoriteNovel(novel);
                favoriteNovel.addMember(member);



                // when
                favoriteNovelRepository.save(favoriteNovel);

                PageRequest pageRequest = PageRequest.of(0, 10);

                List<FavoriteNovel> byMember = favoriteNovelRepository.findByMember(member, pageRequest);



                // then
                // 저장이 반영되는지 확인
                Assertions.assertThat(byMember).contains(favoriteNovel);
            }
        }


        // 즐겨찾기에 보관함 추가 확인
        for (Member member : members) {

            for (Box box : boxes) {

                // given

                Box findBox = boxRepository.findById(box.getId()).get();
                Member findMember = memberRepository.findById(member.getId()).get();

                FavoriteBox favoriteBox = new FavoriteBox(box);
                favoriteBox.addMember(findMember);

                // when
                favoriteBoxRepository.save(favoriteBox);

                PageRequest pageRequest = PageRequest.of(0, 10);

                List<FavoriteBox> byBox = favoriteBoxRepository.findByMember(findMember);


                // then
                // 저장 확인
                Assertions.assertThat(byBox).contains(favoriteBox);
            }
        }


        // 즐겨찾기에 작가 추가 확인
        for (Member member : members) {

            for (Author author : authors) {

                // given

                Author findAuthor = authorRepository.findById(author.getId()).get();
                Member findMember = memberRepository.findById(member.getId()).get();

                FavoriteAuthor favoriteAuthor = new FavoriteAuthor(findAuthor);
                favoriteAuthor.addMember(findMember);

                // when
                favoriteAuthorRepository.save(favoriteAuthor);

                PageRequest pageRequest = PageRequest.of(0, 10);

                List<FavoriteAuthor> byMember = favoriteAuthorRepository.findByMember(findMember, pageRequest);

                // then
                // 저장 확인
                Assertions.assertThat(byMember).contains(favoriteAuthor);
            }
        }




    }


    /**
     *  즐겨찾기 추가 테스트 - 실패 테스트
     *
     *      - 중복 체크
     */
    @DisplayName("즐겨찾기 추가 테스트 (실패 테스트)")
    @Test
    void addFavoriteFail(){

        // 중복되어 들어온 경우
        Novel novel = novelRepository.findAll().get(0);
        Member member = memberRepository.findAll().get(0);
        FavoriteNovel favoriteNovel = new FavoriteNovel(novel);
        favoriteNovel.addMember(member);

        // 처음 저장
        if(favoriteNovelRepository.findByMemberWithNovel(member, novel) == null){
            favoriteNovelRepository.save(favoriteNovel);
        }

        // 한 번더 저장할 때는 예외 던지기
        org.junit.jupiter.api.Assertions.assertThrows(
                DuplicateFavoriteException.class,
                () ->{
                    if(favoriteNovelRepository.findByMemberWithNovel(member, novel) != null){
                        throw new DuplicateFavoriteException();
                    }
                    else{
                        favoriteNovelRepository.save(favoriteNovel);
                    }
                }
        );


    }




    /**
     * 즐겨찾기 삭제 테스트 - 성공 테스트
     *
     * 각 소설, 보관함, 작가의 번호와 사용자의 번호를 가지고 favorite 객체를 찾은 후 삭제를 진행
     */
    @DisplayName("즐겨찾기 삭제 테스트 (성공 테스트 - 소설, 보관함, 작가)")
    @Test
    void delFavorite(){

        //given
        Novel testNovel = novelRepository.findAll().get(0);
        Member testMember = memberRepository.findAll().get(0);
        Author testAuthor = authorRepository.findAll().get(0);
        Box testBox = boxRepository.findAll().get(0);


        FavoriteNovel favoriteNovel = new FavoriteNovel(testNovel);
        favoriteNovel.addMember(testMember);
        favoriteNovelRepository.save(favoriteNovel);

        FavoriteAuthor favoriteAuthor = new FavoriteAuthor(testAuthor);
        favoriteAuthor.addMember(testMember);
        favoriteAuthorRepository.save(favoriteAuthor);

        FavoriteBox favoriteBox = new FavoriteBox(testBox);
        favoriteBox.addMember(testMember);
        favoriteBoxRepository.save(favoriteBox);


        // 삭제
        // when
        FavoriteNovel byMemberWithNovel = favoriteNovelRepository.findByMemberWithNovel(testMember, testNovel);
        favoriteNovelRepository.delete(byMemberWithNovel);

        FavoriteBox byMemberWithBox = favoriteBoxRepository.findByMemberWithBox(testMember, testBox);
        favoriteBoxRepository.delete(byMemberWithBox);

        FavoriteAuthor byMemberWithAuthor = favoriteAuthorRepository.findByMemberWithAuthor(testMember, testAuthor);
        favoriteAuthorRepository.delete(byMemberWithAuthor);


        // then
        PageRequest pageRequest = PageRequest.of(0, 10);

        Assertions.assertThat(favoriteNovelRepository.findByMember(testMember, pageRequest)).isEmpty();
        Assertions.assertThat(favoriteAuthorRepository.findByMember(testMember, pageRequest)).isEmpty();
        Assertions.assertThat(favoriteBoxRepository.findByMember(testMember).isEmpty());

    }


    /**
     * 즐겨찾기 삭제 테스트 - 실패 테스트
     */
    @DisplayName("즐겨찾기 삭제 테스트 (실패 테스트)")
    @Test
    void delFavoriteFail(){

        // 없는 즐겨찾기 번호로 삭제하는 경우
        org.junit.jupiter.api.Assertions.assertThrows(
                WrongFavoriteAccessException.class,
                () -> {
                    if(favoriteNovelRepository.findById(99L).isEmpty())
                        throw new WrongFavoriteAccessException();
                }
        );

    }
}
