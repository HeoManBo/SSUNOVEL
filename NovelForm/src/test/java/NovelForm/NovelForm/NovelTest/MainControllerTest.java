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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 메인 컨트롤러 테스트
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

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
                .cover_image("TESTTEST")
                .review_cnt(1)
                .rating(5.0)
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
    void 메인컨트롤러테스트() throws Exception {
        //given 10명의 작가가 10개의 소설이 있다고 가정하고, 각 웹 사이트 기반 댓글 수는 10만 미만의 무작위 수라고 하자.

        //when : 메인 페이지를 조회한다.
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/novel/main"));

        //result의 상태를 찍어보자.
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print()); //값을 출력해보자
    }
}
