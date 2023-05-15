package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 소설 목록 조회 관련 Controller method test
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class NovelGenreListSearchMethodTest {
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MockMvc mockMvc;

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
    void 소설목록조회메소드테스트_정상처리() throws Exception {
        //given : beforeEach 수행

        ///when : Genre = romance, filtering = default(download_cnt 많은 순) paging = default(0)으로 조회한다.
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/novel")
                .param("genre", "로맨스"));

        //then : 결과 Json 테스트
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 소설목록조회메소드테스트_비정상처리_페이지번호가음수() throws Exception{
        //given : beforeEach 수행

        ///when : Genre = romance, filtering = default(download_cnt 많은 순) paging = -1 으로 조회한다.
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/novel")
                .param("genre", "로맨스")
                .param("pageNum", "-1"));

        //then : BAD_REQUEST 인지 확인한다.
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void 소설목록조회메소드테스트_비정상처리_잘못된필터링() throws Exception{

    }


}
