package NovelForm.NovelForm.NovelTest;

import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * novelRepository findRankingNovel 메소드 테스트 클래스입니다.
 */
@SpringBootTest
@Slf4j
@Transactional
public class MainLogicTest {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private EntityManager em;

    /**
     * 작가 10명, 소설 100개를 만들고, 무작위로 DownLoadCnt를 부여한다.
     */
    Author makeAuthor(String authorName){
        Author author = Author.builder()
                .name(authorName)
                .build();
        authorRepository.save(author);
        return author;
    }

    //테스트 용도이므로 소설의 이름만 넣자.
    void makeNovel(Author author, String title, int download_cnt){
        Novel novel = Novel.builder()
                .title(title)
                .download_cnt(download_cnt)
                .cover_image("sadasd")
                .build();
        novel.addAuthor(author);
        novelRepository.save(novel);
    }

    @BeforeEach
    public void setting_Novel(){
        Random random = new Random();
        // 10명의 작가에서 각 작가가 10개의 소설이 있다고 해보자. 10만 미만의 댓글이 무작위로 있다고 가정한다.
        for(int i=1; i<=10; i++){
            Author author = makeAuthor(i + "번째 작가입니다.");
            for(int j=1; j<=10; j++){
                makeNovel(author, j + "번째 소설입니다.", random.nextInt() % 100000);
            }
        }

        // DB에서 직접 가져오기 위해 영속성 컨텍스트 flush;
        em.flush();
        em.clear();
    }

    @Test
    void 랭킹소설테스트(){
        //given 10명의 작가가 10개의 소설이 있다고 가정하고, 각 웹 사이트 기반 댓글 수는 10만 미만의 무작위 수라고 하자.

        //when : 댓글 수 상위 20개를 점검한다
        List<Novel> findNovels = novelRepository.findByNovelRanking();

        //로그를 찍어보자
        for (Novel findNovel : findNovels) {
            log.info("소설 이름 : {}, 소설 작가 : {}, 리뷰 수 : {}", findNovel.getTitle(), findNovel.getAuthor().getName(), findNovel.getDownload_cnt());
        }

        //가져온 소설 수가 20개인지 확인한다
        //순서는 댓글 수가 무작위로 되어있으므로 확인 X 로그로 확인해보자.
        Assertions.assertThat(findNovels.size()).isEqualTo(20);
    }
}
