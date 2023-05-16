package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;
import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 컨트롤러 단위 테스트
 *
 * @WebMvcTest는 단위테스트용 애노테이션으로 Controller와 관련된 Web layer에 대한 Autowired를 지원한다.
 * 다만, JPA에 대한 bean을 로드하지는 않기 때문에 @WebMvcTest만을 쓸 경우 JPA 관련 bean 등록에 대한 오류가 뜬다.
 * 때문에, @MockBean(JpaMetamodelMappingContext.class)을 등록했다.
 *
 * 스프링 시큐리티를 등록하면서 csrf와 관련된 문제가 생겼다.
 * build.gradle에 security-test를 추가하고 perform에
 * .with(csrf()) 를 추가했다.
 *
 * test에 profile 적용... applicaiton-test.properties를 우선시 해서 받게 처리했다.
 */
@WebMvcTest(controllers = MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    MemberController memberController;

    @MockBean
    private MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 컨트롤러 테스트를 위한 MockMvc설정 및 해당 컨트롤러 생성 하는 부분
     *
     * @SpringbootTest를 쓰지 않기에 @Autowired가 불가능
     */
//    @BeforeEach
//    void setMockMvc(){
//        this.mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
//    }

    @Test
    @DisplayName("[단위] 회원 가입 / 요청이 정상적으로 들어오는가?, Validation 이 성공하는가?")
    @WithMockUser(username = "테스트관리자", roles = "USER")
    void createMember() throws Exception {

        // given
        // body에 들어갈 값들 정의
        ArrayList<CreateMemberRequest> testList = new ArrayList<>();

        // null 체크
        testList.add(new CreateMemberRequest("test@naver.com", null, "testNickname", null, "2000-01-01"));

        // 길이 체크
        testList.add(new CreateMemberRequest("test@naver.com", "12345", "testNickname", Gender.MALE, "2000-01-01"));

        // 이메일 형식 체크
        testList.add(new CreateMemberRequest("testnaver.com", "1234567", "testNickname", Gender.MALE, "2000-01-01"));

        // 정상 통과 체크
        CreateMemberRequest successTest =
                new CreateMemberRequest("test@naver.com", "123456789", "testNickname", Gender.MALE,"2000-01-01");





        // when
        // mockMvc로 post 요청을 보내기
        // 실패 테스트
        for (CreateMemberRequest createMemberRequest : testList) {
            mockMvc.perform(post("/member/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createMemberRequest))
                            .accept(MediaType.APPLICATION_JSON)
                            .with(csrf())
                    )
        // then
                    .andExpect(status().isBadRequest());
        }



        // when
        // 성공 테스트
        mockMvc.perform(post("/member/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successTest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
        // then
                .andExpect(status().isOk());

    }





    @Test
    @DisplayName("[단위] 로그인 / 요청이 잘 들어오는가?")
    @WithMockUser(username = "테스트관리자", roles = "USER")
    void loginMember() throws Exception {

        // given
        // body에 들어갈 값들 정의
        ArrayList<LoginMemberRequest> testList = new ArrayList<>();

        // null 체크
        testList.add(new LoginMemberRequest("test@naver.com", null));

        // 길이 체크
        testList.add(new LoginMemberRequest("test@naver.com", "12345"));

        // 이메일 형식 체크
        testList.add(new LoginMemberRequest("testnaver.com", "1234567"));

        // 정상 통과 체크
        LoginMemberRequest successTest =
                new LoginMemberRequest("test@naver.com", "123456789");


        // when
        // mockMvc로 post 요청을 보내기
        // 실패 테스트
        for (LoginMemberRequest loginMemberRequest : testList) {
            mockMvc.perform(post("/member/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginMemberRequest))
                            .accept(MediaType.APPLICATION_JSON)
                            .with(csrf())
                    )
        // then
                    .andExpect(status().isBadRequest());
        }


        // when
        // 성공 테스트
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successTest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
        // then
                .andExpect(status().isOk());


    }
}