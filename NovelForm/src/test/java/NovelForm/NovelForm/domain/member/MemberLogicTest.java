package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;

import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.repository.MemberRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


/**
 * 회원 가입 등의 시나리오 로직등의 통합 테스트
 *
 * 테스트 DB는 H2를 사용한다.
 *  TestRestTemplate을 사용해 테스트를 하는 경우 DB에 저장되기 때문에 매번 삭제를 해야 한다.
 *
 * bean 중복 정의 문제가 생겼다.
 * The bean 'memberRepository', defined in NovelForm.NovelForm.repository.MemberRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration, could not be registered. A bean with that name has already been defined in file [C:\Users\jinsoo\Desktop\SSUNovel\NovelForm\out\production\classes\NovelForm\NovelForm\domain\member\MemberRepository.class] and overriding is disabled.
 *
 * 그래서 spring.main.allow-bean-definition-overriding=true를 properties에 추가했다.
 *
 * 통합 테스트에서는 TemplateRestTest를 사용했다.
 *
 * test에 profile 적용... applicaiton-test.properties를 우선시 해서 받게 처리했다.
 */

@Slf4j
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MemberLogicTest {

    @Autowired
    private MemberController memberController;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder encoder;


    // request를 보내기 위한 TestRestTemplate
    @Autowired
    private TestRestTemplate restTemplate;


    /**
     *  회원가입 서비스 통합 테스트
     */
    @Test
    @DisplayName("[통합] 회원가입로직")
    @Transactional
    @Rollback(true)
    public void createMember() throws Exception {
        // given
        // request에 들어갈 body 부분 정의
        String email = "test999@naver.com";
        String password = "12345678";

        CreateMemberRequest testDto = new CreateMemberRequest(
                email,
                password,
                "testNickname",
                Gender.MALE,
                "2000-01-01");

        ObjectMapper objectMapper = new ObjectMapper();

        String url = "http://localhost:8080" + "/member/create";

        // json 타입이 body에 들어가기에 헤더에 content-type 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 헤더를 포함해 HttpEntity 생성
        HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(testDto), headers);

        // when
        // post 요청 보내기
        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                url,
                httpEntity,
                BaseResponse.class);


        // then
        // 요청이 성공했는지 먼저 확인
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // save가 정상적으로 수행 되었는지 확인한다.
        Assertions.assertThat(memberRepository.findByEmail(email)).isNotNull();

        // 비밀번호가 암호화 되어 들어갔는지 확인한다.
        //  match를 통해 암호화 된 패스워드와 암호화 하기 전의 패스워드가 일치하는지 확인한다.
        Assertions.assertThat(encoder.matches(password, memberRepository.findByEmail(email).getPassword())).isTrue();;

        // 세션이 생성 되었는지 확인한다.
        Assertions.assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();



        // when
        // post 요청 한 번 더 보내기
        response = restTemplate.postForEntity(
                url,
                httpEntity,
                BaseResponse.class);

        // then
        // 중복 체크에 걸리는가?
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }


    /**
     *  로그인 로그아웃 로직 확인
     */
    @Test
    @DisplayName("[통합] 로그인/로그아웃 로직")
    @Transactional
    @Rollback(true)
    public void loginMember() throws JsonProcessingException {

        // given
        // request에 들어갈 body 부분 정의
        String email = "test888@naver.com";
        String password = "12345678";



        // 정상 적인 회원...
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                email,
                password,
                "testn",
                Gender.stringToGender("MALE"),
                "2000-01-01"
        );
        LoginMemberRequest testDto = new LoginMemberRequest(email, password);

        // DB에 없는 회원...
        LoginMemberRequest emptyTestDto = new LoginMemberRequest("test@ggg.com", "87654321");

        // 비밀번호가 잘못 된 회원...
        LoginMemberRequest wrongPwDto = new LoginMemberRequest(email, "12312312");

        ObjectMapper objectMapper = new ObjectMapper();

        String url = "http://localhost:8080";

        // json 타입이 body에 들어가기에 헤더에 content-type 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 헤더를 포함해 HttpEntity 생성
        // 회원 생성 엔티티
        HttpEntity<String> createEntity = new HttpEntity<>(objectMapper.writeValueAsString(createMemberRequest), headers);

        // 로그인 헤더 엔티티
        HttpEntity<String> loginEntity = new HttpEntity<>(objectMapper.writeValueAsString(testDto), headers);

        // emptyTestDto 헤더 엔티티
        HttpEntity<String> emptyTestEntity = new HttpEntity<>(objectMapper.writeValueAsString(emptyTestDto), headers);

        // wrongPwDto 헤더 엔티티
        HttpEntity<String> wrongTestEntity = new HttpEntity<>(objectMapper.writeValueAsString(wrongPwDto), headers);




        // when
        // post 요청 보내기
        // 먼저 회원을 하나 생성
        restTemplate.postForEntity(
                url + "/member/create",
                createEntity,
                BaseResponse.class);


        // 로그인 post 요청 보내기
        // wrongTestRes : 잘못된 비밀번호
        ResponseEntity<BaseResponse> wrongTestRes = restTemplate.postForEntity(
                url + "/member/login",
                wrongTestEntity,
                BaseResponse.class);

        // emptyTestRes : 비어있는 유저
        ResponseEntity<BaseResponse> emptyTestRes = restTemplate.postForEntity(
                url + "/member/login",
                emptyTestEntity,
                BaseResponse.class
        );

        // baseRes : 정상 수행 로그인
        ResponseEntity<BaseResponse> baseRes = restTemplate.postForEntity(
                url + "/member/login",
                loginEntity,
                BaseResponse.class
        );


        // then

        // 비밀번호가 일치하지 않을 때 거를 수 있는가?
        Assertions.assertThat(wrongTestRes.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // DB에 없는 이메일을 거를 수 있는가?
        Assertions.assertThat(emptyTestRes.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // 정상 요청이 받아지는가?
        Assertions.assertThat(baseRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 세션이 생성 되는가?
        Assertions.assertThat(baseRes.getHeaders().get("Set-Cookie")).isNotNull();



        // 로그아웃 요청 전달
        // 위에서 만든 쿠키 정보를 같이 전달
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, baseRes.getHeaders().get("Set-Cookie").get(0));

        ResponseEntity<BaseResponse> logoutResponse =
                restTemplate.exchange(url + "/member/logout",
                        HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders),
                        BaseResponse.class);


        // 요청이 성공했는가?
        Assertions.assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 세션이 사라졌는가?
        Assertions.assertThat(logoutResponse.getHeaders().get("set-Cookie")).isNull();

        // 로그아웃이 된 상황에서 로그아웃을 다시 호출했을 때, BAD_REQUEST를 내보내는가?
        logoutResponse = restTemplate.exchange(url + "/member/logout",
                        HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders),
                        BaseResponse.class);

        Assertions.assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }



}
