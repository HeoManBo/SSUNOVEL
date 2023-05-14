package NovelForm.NovelForm.domain.box;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.CreateBoxRequest;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.SessionConst;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.BoxRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 *  보관함 생성 및 삭제 요청이 정상적으로 들어오는지에 대한 테스트
 *
 *  보관함 수정의 경우 삭제 -> 생성 순으로 진행되기 때문에 controller 단의 테스트는 진행하지 않았다.
 *  삭제의 경우 인자로 전달 받는 값이 pathvariable과 sessionAttribute로 받는 값이라 통합 테스트에서 한 번에 진행했다.
 */
@WebMvcTest(controllers = BoxController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BoxControllerTest {

    @Autowired
    BoxController boxController;

    @MockBean
    BoxService boxService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    BoxRepository boxRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    NovelRepository novelRepository;

    @MockBean
    AuthorRepository authorRepository;



    @DisplayName("[단위] 보관함 생성 요청 검사 테스트")
    @Test
    @WithMockUser(username = "테스트관리자", roles = "USER")
    void createBox() throws Exception {

        // given
        
        // 보관함 생성을 위한 세션
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);


        // 실패할 테스트를 담을 리스트
        List<CreateBoxRequest> failTestList = new ArrayList<>();


        // BoxItem에 넣을 작품 번호들
        List<Long> testList = new ArrayList<>();
        testList.add(6L);


        // null 데이터 체크
        failTestList.add(
                new CreateBoxRequest(
                        null,
                        "test Content11",
                        0,
                         testList,
                        6L)
        );

        // BoxItems으로 넣은 작품 번호들에 대표작품 번호가 포함되어 있는지 체크
        failTestList.add(
                new CreateBoxRequest(
                        "test title11",
                        "test Content11",
                        1,
                        testList,
                        1L)
        );

        CreateBoxRequest successTest = new CreateBoxRequest(
                "test title11",
                "test Content11",
                0,
                testList,
                6L);


        // when
        for (CreateBoxRequest createBoxRequest : failTestList) {
            mockMvc.perform(post("/box")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createBoxRequest))
                    .with(csrf())
            )

            // then
                    .andExpect(status().isBadRequest());
        }



        // when
        mockMvc.perform(post("/box")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successTest))
                        .with(csrf()))

        // then
                .andExpect(status().isOk());



    }
}