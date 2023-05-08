package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.dto.NovelDto;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 소설 검색 테스트 클래스
 *
 */
@SpringBootTest
@Slf4j
@Transactional
public class SearchPagingTest {

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em; //영속성 컨텍스트를 비우기 위해

    Author makeAuthor(String authorName){
        Author author = Author.builder()
                .name(authorName)
                .build();
        authorRepository.save(author);
        return author;
    }

    //테스트 용도이므로 소설의 이름만 넣자.
    void makeNovel(Author author, String title){
        Novel novel = Novel.builder()
                .title(title)
                .build();
        novel.addAuthor(author);
        novelRepository.save(novel);
    }

    @BeforeEach
    public void setting_Novel(){
        // 10명의 작가에서 각 작가가 10개의 소설이 있다고 해보자.
        for(int i=1; i<=10; i++){
            Author author = makeAuthor(i + "번째 작가입니다.");
            for(int j=1; j<=10; j++){
                makeNovel(author, j + "번째 소설입니다.");
            }
        }

        // DB에서 직접 가져오기 위해 영속성 컨텍스트 flush;
        em.flush();
        em.clear();
    }

    @Test
    public void 페이징테스트_소설(){
        //given : 10명의 작가가 있고, 각 작가는 10개의 소설을 작성했다고 가정한다.

        //when : 1번째라는 이름을 가진 소설을 검색했을 때, 2번 페이지의 항목들을 보고 싶다고 하자.
        Pageable pageable = PageRequest.of(1, 5); // 6~10번 작가의 소설을 가져오자.
        Page<Novel> novels = novelRepository.findByTitleWithPaging("1번째", pageable);
        int novelCnt = novelRepository.countWithMatchingTitle("1번째");  //1번째라는 이름이 포함된 소설 제목의 총 개수는 10개

        //then : 1번째라는 이름을 가진 소설은 총 10개이고 페이징 단위가 5이므로 총 2개의 페이지가 나온다.
        //또한 전체 소설 개수는 10개가 된다.
        List<Novel> result = novels.stream().toList();
        log.info("총 소설 개수는 {}개", novelCnt);
        //각 소설의 정보를 찍어보자
        for (Novel novel : result) {
            log.info("작가 이름 : {}, 소설 이름 : {} ", novel.getAuthor().getName(), novel.getTitle());
        }
        assertThat(novelCnt).isEqualTo(10); //총 개수는 10개임.
        assertThat(novels.getTotalPages()).isEqualTo(2); // '1번째'라는 소설이름은 10개이므로 페이징을 5단위로 했으므로 전체 페이진 2개여야 함.
        assertThat(novels.getPageable().getPageNumber()).isEqualTo(1); // 페이지 번호는 1번이여야함.
        assertThat(novels.getPageable().getPageSize()).isEqualTo(5); //각 페이지별로 5개의 소설이 있어야함
        assertThat(novels.getTotalElements()).isEqualTo(10); // '1번째'라는 소설은 10개를 만들었으므로 조건일치 소설 수는 10개여야함.
    }


    @Test
    public void 페이징테스트_작가(){
        //given : 10명의 작가가 있고, 각 작가는 10개의 소설을 작성했다고 가정한다.

        //when : 각 작가는 10개의 소설을 가지고 있으므로 5번 작가의 6~10번 소설을 조회해보자.
        Pageable pageable = PageRequest.of(1, 5);
        Page<Novel> novels = novelRepository.findByAuthorWithPaging("5번째", pageable);
        int countWithMatchingAuthorName = novelRepository.countWithMatchingAuthorName("5번째"); //해당 작가의 총 작품 개수

        //then : 5번째 작가가 쓴 소설은 총 10개이므로 개수는 10개가 된다.
        List<Novel> result = novels.stream().toList();
        log.info("5번째 작가의 총 작품 수는 {}개", countWithMatchingAuthorName);
        //각 소설의 정보를 찍어보자
        for (Novel novel : result) {
            log.info("작가 이름 : {}, 소설 이름 : {} ", novel.getAuthor().getName(), novel.getTitle());
        }
        assertThat(countWithMatchingAuthorName).isEqualTo(10); //각 작가는 10개의 소설을 가지고 있음
        assertThat(novels.getTotalPages()).isEqualTo(2); //  5번째작가는 전체 10개의 소설을 가지므로 1:5, 2:5 개를 갖는 페이지를 2개 갖는다.
        assertThat(novels.getPageable().getPageNumber()).isEqualTo(1); // 페이지 번호는 1번이여야함.
        assertThat(novels.getPageable().getPageSize()).isEqualTo(5); //각 페이지별로 5개의 소설이 있어야함
        assertThat(novels.getTotalElements()).isEqualTo(10); // '5번째작가는 전체 10개의 소설을 가지므로 조건일치 소설 수는 10개여야함.
    }


}
