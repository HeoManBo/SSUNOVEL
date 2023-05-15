package NovelForm.NovelForm.NovelTest;


import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SearchControllerTest {

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
    public void 검색테스트_소설() throws Exception {
        //given : 페이징과 동일한 조건 (10명의 작가가 작가당 10개의 작품을 가지고 있음)

        //when : '1번째' 라는 소설을 조회해보자.
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/novel/search")
                .param("search", "1번째 소설입니다.")
                .accept(MediaType.APPLICATION_JSON));

        //then : 결과 json을 출력해보자
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk()) //httpStatus는 OK여야함.
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.forNovel.count").value(10)) //예상 개수는 10개
                .andDo(print()); //조회 결과 출력
    }

    @Test
    public void 검색테스트_작가() throws Exception {
        //given : 페이징과 동일한 조건 (10명의 작가가 작가당 10개의 작품을 가지고 있음)

        //when : '5번째' 라는 작가의 소설을 조회해보자.
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/novel/search")
                .param("search", "5번째 작가입니다")
                .accept(MediaType.APPLICATION_JSON));

        //then : 결과 json을 출력해보자
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk()) //httpStatus는 OK여야함.
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.forAuthor.count").value(10)) //예상 개수는 10개
                .andDo(print()); //조회 결과 출력
    }

    @Test
    public void 검색테스트_입력값없음() throws Exception {
        //given : 페이징과 동일한 조건 (10명의 작가가 작가당 10개의 작품을 가지고 있음)

        //when : 검색어가 없는("")경우를 고려해보자 -> bad_request여야함
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/novel/search")
                .param("search", "")
                .accept(MediaType.APPLICATION_JSON));

        //then : 결과 json을 출력해보자
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk()) //httpStatus는 OK;
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("BAD_REQUEST")) //내부코드가 bad
                .andDo(print()); //조회 결과 출력
    }

    @Test
    public void 검색테스트_입력값공백() throws Exception {
        //given : 페이징과 동일한 조건 (10명의 작가가 작가당 10개의 작품을 가지고 있음)

        //when : 검색어가 공백인("    ")경우를 고려해보자 -> bad_request여야함
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/novel/search")
                .param("search", "     ")
                .accept(MediaType.APPLICATION_JSON));

        //then : 결과 json을 출력해보자
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk()) //httpStatus는 OK;
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("BAD_REQUEST")) //내부코드가 bad
                .andDo(print()); //조회 결과 출력
    }

}
