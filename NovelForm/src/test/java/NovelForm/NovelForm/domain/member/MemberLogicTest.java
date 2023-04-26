package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;

import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.repository.MemberRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


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
 */

@Slf4j
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
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



    @Test
    @DisplayName("[통합] 회원가입로직")
    public void createMember() throws Exception {
        // given
        // request에 들어갈 body 부분 정의
        String email = "test999@naver.com";
        String password = "12345678";

        CreateMemberRequest testDto = new CreateMemberRequest(
                email,
                password,
                "testNickname",
                Gender.MALE);

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

        log.info("result = {}", response);

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

}
