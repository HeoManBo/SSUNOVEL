package NovelForm.NovelForm;


import NovelForm.NovelForm.domain.favorite.FavoriteNovel;
import NovelForm.NovelForm.domain.member.Gender;
import NovelForm.NovelForm.domain.member.LoginType;
import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.Platform;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.FavoriteNovelRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BasicTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private FavoriteNovelRepository favoriteNovelRepository;

    @Test
    void 멤버생성테스트(){
        Member member1 = RegisterMember("asdf@naver.com", "ssu");
        Member member2 = RegisterMember("aaaa@naver.com", "ssu");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        System.out.println(findMember1.toString());
        System.out.println(findMember2.toString());
        assertThat(findMember1.getId()).isEqualTo(member1.getId());
        assertThat(findMember2.getId()).isEqualTo(member2.getId());
    }

    //이메일은 중복되면 안되기 때문에 검사해야함.
    @Test
    void 멤버중복생성테스트(){
        Member member1 = RegisterMember("asdf@naver.com", "ssu");
        Member member2 = RegisterMember("asdf@naver.com", "ssu");

        memberRepository.save(member1);

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () ->{
            memberRepository.save(member2);
        });
    }

    @Test
    void 소설생성테스트(){
        Author author = RegisterAuthor("숭숭숭");
        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};
        Novel novel = RegisterNovel("후후후후후", author,day,list);

        authorRepository.save(author);
        novelRepository.save(novel);

        Novel findNovel = novelRepository.findById(novel.getId()).get();
        System.out.println("findNovel.toString() = " + findNovel.toString());

        assertThat(novel.getId()).isEqualTo(findNovel.getId());
    }

    @Test
    void 즐겨찾기생성(){
        Member member1 = RegisterMember("asdf@naver.com", "ssu");

        memberRepository.save(member1);

        List<Novel> novels = new ArrayList<>();

        Author author = RegisterAuthor("숭숭숭");
        authorRepository.save(author);

        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};

        Novel novel1 = RegisterNovel("1번소설", author,day,list);
        Novel novel2 = RegisterNovel("2번소설", author,day,list);
        Novel novel3 = RegisterNovel("3번소설", author,day,list);
        Novel novel4 = RegisterNovel("4번소설", author,day,list);

        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);

        novelRepository.saveAllAndFlush(novels);

        /**
         * 즐겨 찾기 생성
         */
        FavoriteNovel favoriteNovel1 = new FavoriteNovel();
        favoriteNovel1.addMember(member1);
        favoriteNovel1.addNovel(novel1);
        favoriteNovelRepository.save(favoriteNovel1);

        FavoriteNovel favoriteNovel2 = new FavoriteNovel();
        favoriteNovel2.addMember(member1);
        favoriteNovel2.addNovel(novel2);
        favoriteNovelRepository.save(favoriteNovel2);

        List<FavoriteNovel> findNovels = favoriteNovelRepository.findByMember(member1);
        List<Novel> result = new ArrayList<>();
        for (FavoriteNovel findNovel : findNovels) {
            System.out.print("member nickname = " + findNovel.getMember().getNickname());
            System.out.println(" find Novel title " + findNovel.getNovel().getTitle());
            result.add(findNovel.getNovel());
        }

        assertThat(result.get(0)).isEqualTo(novel1);
        assertThat(result.get(1)).isEqualTo(novel2);

    }



    //작가명으로 찾기
    @Test
    void 소설검색테스트_소설명1(){
        //when
        String search1 = "2번소설";

        List<Novel> novels = new ArrayList<>();

        Author author = RegisterAuthor("숭숭숭");
        authorRepository.save(author);

        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};

        Novel novel1 = RegisterNovel("1번소설", author,day,list);
        Novel novel2 = RegisterNovel("2번소설", author,day,list);
        Novel novel3 = RegisterNovel("3번소설", author,day,list);
        Novel novel4 = RegisterNovel("4번소설", author,day,list);

        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);

        novelRepository.saveAllAndFlush(novels);

        //then
        List<Novel> result1 = novelRepository.findByTitleName(search1);


        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0).getTitle()).isEqualTo("2번소설");
    }

    @Test
    void 소설검색테스트_소설명2(){
        String search2 = "2번";

        List<Novel> novels = new ArrayList<>();

        Author author = RegisterAuthor("숭숭숭");
        authorRepository.save(author);

        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};

        Novel novel1 = RegisterNovel("1번소설", author,day,list);
        Novel novel2 = RegisterNovel("2번소설", author,day,list);
        Novel novel3 = RegisterNovel("3번소설", author,day,list);
        Novel novel4 = RegisterNovel("4번소설", author,day,list);

        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);

        novelRepository.saveAllAndFlush(novels);

        List<Novel> result = novelRepository.findByTitleName(search2);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("2번소설");
    }

    @Test
    void 소설검색테스트_소설명3(){
        String search2 = "소설";

        List<Novel> novels = new ArrayList<>();

        Author author = RegisterAuthor("숭숭숭");
        authorRepository.save(author);

        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};

        Novel novel1 = RegisterNovel("1번소설", author,day,list);
        Novel novel2 = RegisterNovel("2번소설", author,day,list);
        Novel novel3 = RegisterNovel("3번소설", author,day,list);
        Novel novel4 = RegisterNovel("4번소설", author,day,list);

        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);

        novelRepository.saveAllAndFlush(novels);

        List<Novel> result = novelRepository.findByTitleName(search2);

        for (Novel novel : result) {
            System.out.println("novel = " + novel.getTitle());
        }

        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    void 소설검색테스트_작가명(){
        String search1 = "홍";

        List<Novel> novels = new ArrayList<>();

        Author author = RegisterAuthor("숭숭숭");
        Author author2 = RegisterAuthor("홍홍홍");

        authorRepository.save(author);
        authorRepository.save(author2);

        char[] day = {'n','y','n','n','n','n','n'};
        char[] list = {'y','n','n','n'};

        Novel novel1 = RegisterNovel("1번소설", author,day,list);
        Novel novel2 = RegisterNovel("2번소설", author,day,list);
        Novel novel3 = RegisterNovel("3번소설", author,day,list);
        Novel novel4 = RegisterNovel("4번소설", author2,day,list);
        Novel novel5 = RegisterNovel("5번소설", author2,day,list);

        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);
        novels.add(novel5);

        novelRepository.saveAllAndFlush(novels);

        List<Novel> result = novelRepository.findByAuthorName(search1);

        for (Novel novel : result) {
            System.out.println("소설 제목 : " + novel.getTitle() + " 작가 명 : " + novel.getAuthor().getName());
        }

        assertThat(result.size()).isEqualTo(2);

    }

   /* @Test
    void 즐겨찾기조회(){
        Member member1 = RegisterMember("asdf@naver.com", "ssu");

        memberRepository.save(member1);
        Optional<Member> findMember = memberRepository.findById(4L);
        Member member = null;
        if(findMember.isPresent()){
           member = findMember.get();
            System.out.println(member.toString());
        }
        else {
            System.out.println("멤버를 찾을 수 없습니다..");
            return;
        }

        List<FavoriteNovel> findFavoriteNovels = favoriteNovelRepository.findByMember(member);

        if(findFavoriteNovels.size() == 0){
            System.out.println("즐겨찾기한 소설을 찾을 수 없습니다..");
            return;
        }


        for (FavoriteNovel findFavoriteNovel : findFavoriteNovels) {
            System.out.println(findFavoriteNovel.getNovel().getTitle());
        }

        assertThat(findFavoriteNovels.size()).isEqualTo(2);
    }
*/

    //테스트 전용 메소드
    private Member RegisterMember(String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .password("1234")
                .nickname(nickname)
                .gender(Gender.MALE)
                .loginType(LoginType.USER)
                .build();
        return member;
    }


    private Platform registerPlatform(char[] list){
        if(list.length != 4) return null;

        Platform platform = Platform.builder()
                .naver(list[0])
                .kakao(list[1])
                .munpia(list[2])
                .ridibooks(list[3])
                .build();

        return platform;
    }

    private Novel RegisterNovel(String name, Author author, char[] day, char[] list){
        Novel novel = Novel.builder()
                .title(name)
                .download_cnt(0)
                .rating(0.0)
                .review_cnt(0)
                .summary("테스트 입력")
                .build();

        novel.addAuthor(author);

        return novel;
    }

    private Author RegisterAuthor(String name){
        Author author = Author.builder()
                .name(name)
                .build();
        return author;
    }

}
