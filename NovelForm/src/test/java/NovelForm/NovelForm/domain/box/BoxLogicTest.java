package NovelForm.NovelForm.domain.box;


import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.CreateBoxRequest;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;
import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.SessionConst;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.BoxRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BoxLogicTest {

    @Autowired
    private BoxController boxController;

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private MemberRepository memberRepository;


    // request를 보내기 위한 TestRestTemplate
    @Autowired
    private TestRestTemplate restTemplate;


    @Autowired
    EntityManager em;


    @BeforeEach
    void setBox(){

        Author testAuthor1 = new Author("test Author1");
        Author testAuthor2 = new Author("test Author2");
        Author testAuthor3 = new Author("test Author3");

        authorRepository.save(testAuthor1);
        authorRepository.save(testAuthor2);
        authorRepository.save(testAuthor3);

        Novel testNovel1 = new Novel(
                "test title1",
                "test summary1",
                121,
                100,
                390,
                "연재중",
                "https://img/src",
                3.8,
                198,
                "판타지",
                testAuthor1,
                0,
                1,
                1,
                1
        );

        Novel testNovel2 = new Novel(
                "test title2",
                "test summary2",
                1211,
                100,
                3901,
                "연재중",
                "https://img/src22222",
                3.8,
                1981,
                "판타지",
                testAuthor2,
                0,
                1,
                1,
                1
        );

        Novel testNovel3 = new Novel(
                "test title3",
                "test summary333",
                1213,
                100,
                39033,
                "연재중",
                "https://img/src333",
                9.8,
                198,
                "판타지",
                testAuthor3,
                0,
                1,
                1,
                1
        );

        Novel testNovel4 = new Novel(
                "test title4",
                "test summary4",
                12124,
                100,
                39024,
                "연재중",
                "https://img/src444",
                3.8,
                198,
                "판타지",
                testAuthor1,
                0,
                1,
                1,
                1
        );

        Novel testNovel5 = new Novel(
                "test title5",
                "test summary555",
                121,
                100,
                390,
                "연재중",
                "https://img/src555",
                3.8,
                198,
                "판타지",
                testAuthor2,
                0,
                1,
                1,
                1
        );

        Novel testNovel6 = new Novel(
                "test title6",
                "test summary666",
                1216,
                100,
                3906,
                "연재중",
                "https://img/src666",
                3.8,
                1986,
                "판타지",
                testAuthor3,
                0,
                1,
                1,
                1
        );


        novelRepository.save(testNovel1);
        novelRepository.save(testNovel2);
        novelRepository.save(testNovel3);
        novelRepository.save(testNovel4);
        novelRepository.save(testNovel5);
        novelRepository.save(testNovel6);


        Member member1 = new Member(
                "tm1@naver.com",
                "12123838",
                "tmName11",
                Gender.MALE,
                LoginType.USER,
                38
        );

        Member member2 = new Member(
                "tm122@naver.com",
                "12123838",
                "tmName22",
                Gender.MALE,
                LoginType.USER,
                38
        );

        Member member3 = new Member(
                "tm133@naver.com",
                "12123838",
                "tmName33",
                Gender.MALE,
                LoginType.USER,
                38
        );


        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);


        BoxItem testItem1 = new BoxItem(1, testNovel1);
        BoxItem testItem2 = new BoxItem(0, testNovel2);
        BoxItem testItem3 = new BoxItem(0, testNovel3);



        Box testBox1 = new Box("box title 1", "content 1", 0, member1);
        testBox1.addBoxItem(testItem1);
        testBox1.addBoxItem(testItem2);
        testBox1.addBoxItem(testItem3);
        boxRepository.save(testBox1);
    }



    @Transactional
    @DisplayName("[통합] 보관함 생성 및 보관함 상세정보 가져오기 및 보관함 전체 목록 가져오기 테스트")
    @Test
    public void getAllBox() throws JsonProcessingException {

        // given
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders loginHeader = new HttpHeaders();
        loginHeader.set("Content-Type", "application/json");


        CreateMemberRequest createMemberRequest = new CreateMemberRequest("tm999@naver.com", "12123838", "tnname", Gender.MALE,23);
        LoginMemberRequest loginMemberRequest = new LoginMemberRequest("tm999@naver.com", "12123838");

        // 회원 가입
        HttpEntity<String> createHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(createMemberRequest), loginHeader);
        //restTemplate.postForEntity("http://localhost:8080/member/create", createHttpEntity, BaseResponse.class);

        
        // 헤더를 포함해 HttpEntity 생성
        HttpEntity<String> loginHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(loginMemberRequest), loginHeader);

        ResponseEntity<BaseResponse> loginResponse = restTemplate.postForEntity(
                "http://localhost:8080/member/login",
                loginHttpEntity,
                BaseResponse.class
        );




        // 테스트 작가 추가
        Author testAuthor = new Author("some author");
        authorRepository.save(testAuthor);


        Novel testNovel = new Novel(
                "novel title",
                "novel summary",
                100,
                100,
                121,
                "완결",
                "http://src/image1",
                9.8,
                38,
                "판타지",
                testAuthor,
                1,
                0,
                0,
                0);

        Novel save = novelRepository.save(testNovel);


        // 보관함 아이템
        List<Long> testBoxItems = new ArrayList<>();
        testBoxItems.add(save.getId());



        Optional<Novel> byId = novelRepository.findById(save.getId());

        // 보관함 요청 생성
        CreateBoxRequest testDto = new CreateBoxRequest(
                "test title",
                "some content",
                0,
                testBoxItems,
                byId.get().getId());




        System.out.println("empty:" + byId.isEmpty());

        System.out.println("isPresent:" + byId.isPresent());

        System.out.println("id :" + byId.get().getId());

        System.out.println("--->: " + testDto.getBoxItems().contains(byId.get().getId()));


        // json 타입이 body에 들어가기에 헤더에 content-type 설정
        // 세션 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Cookie", loginResponse.getHeaders().getFirst("Set-Cookie"));


        // 헤더를 포함해 HttpEntity 생성
        HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(testDto), headers);


        String url = "http://localhost:8080" + "/box";


        // when
        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                url,
                httpEntity,
                BaseResponse.class
        );

        //Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);




    }



}
