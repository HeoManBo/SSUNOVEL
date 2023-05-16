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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 소설 목록 조회 관련 Repository method test
 */
@SpringBootTest
@Slf4j
@Transactional
public class NovelGenreListSearchTest {
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private AuthorRepository authorRepository;


    /**
     * 로맨스 장르 소설 35개 생성, 작가는 5명으로 하자, 7 * 5 = 35;
     */
    Author makeAuthor(String authorName){
        Author author = Author.builder()
                .name(authorName)
                .build();
        authorRepository.save(author);
        return author;
    }

    //테스트 용도이므로 소설의 이름만 넣자.
    void makeNovel(Author author, String title, String genre, int i){
        Novel novel = Novel.builder()
                .title(title)
                .category(genre)
                .download_cnt(i * 10000 - i * 10)
                .cover_image(i + ": IMAGE COVER")
                .build();
        novel.addAuthor(author);
        novelRepository.save(novel);
    }

    @BeforeEach
    void beforeEach(){
        for(int i=1; i<=5; i++){
            Author author = makeAuthor(i + "번째 작가입니다.");
            for(int j=1; j<=7; j++){
                makeNovel(author, j + "번째 소설입니다.", "로맨스", j);
            }
        }
        em.flush();
        em.clear();
    }

    @Test
    void 소설목록조회테스트(){
        // given : beforeEach 수행

        //when : Genre = romance, filtering = default(download_cnt 많은 순) paging = default(0)으로 조회한다.
        Pageable pageable = PageRequest.of(1, 10, Sort.by("download_cnt").descending());
        Page<Novel> find = novelRepository.findByGenreWithFiltering("로맨스", pageable);
        List<Novel> novels = find.getContent();
        //then : 전체 소설 35개가 잘 조회됐는지 확인한다.
        assertThat(find.getTotalElements()).isEqualTo(35);
        //then : novels의 크기가 10개인지 확인한다.
        assertThat(novels.size()).isEqualTo(10);
        // 정보를 출력해보자.
        for (Novel novel : novels) {
//            log.info("소설 이름 : {}, 이미지 URL : {}, 작가 이름 : {}, 리뷰 수 : {}, 장르 : {}", novel.getTitle(), novel.getAuthor().getName(),
//                    novel.getCover_image(), novel.getDownload_cnt(), novel.getCategory());
        }
    }

    @Test
    void 소설목록조회테스트_예외테스트_페이지번호(){
        // given : beforeEach 수행

        //when : Genre = romance, filtering = default(download_cnt 많은 순) paging = 100 -> 존재하지 않음
        Pageable pageable = PageRequest.of(100, 10, Sort.by("download_cnt").descending());
        Page<Novel> find = novelRepository.findByGenreWithFiltering("로맨스", pageable);
        List<Novel> novels = find.getContent();
        //then : 전체 소설 35개가 잘 조회됐는지 확인한다.
        assertThat(find.getTotalElements()).isEqualTo(35);
        //then : novels의 크기가 10개인지 확인한다.
        assertThat(novels.size()).isEqualTo(0);
        //데이터가 없으므로 공리스트임.
        assertThat(novels.isEmpty()).isEqualTo(true);
    }

    @Test
    void 소설목록조회테스트_예외테스트_잘못된장르이름(){
        //given : beforeEach 수행

        //when : Genre = TEST (존재하지 않는 장르),  filtering = default(download_cnt 많은 순) paging = default(0)으로 조회한다.
        Pageable pageable = PageRequest.of(1, 10, Sort.by("download_cnt").descending());
        Page<Novel> find = novelRepository.findByGenreWithFiltering("TEST", pageable);
        List<Novel> novels = find.getContent();
        //then : 전체 소설 35개가 잘 조회됐는지 확인한다.
        assertThat(find.getTotalElements()).isEqualTo(0);
        //then : novels의 크기가 10개인지 확인한다.
        assertThat(novels.size()).isEqualTo(0);
        //데이터가 없으므로 공리스트임.
        assertThat(novels.isEmpty()).isEqualTo(true);
    }

}
