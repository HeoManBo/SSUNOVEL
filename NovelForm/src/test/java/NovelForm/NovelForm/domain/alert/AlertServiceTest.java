package NovelForm.NovelForm.domain.alert;

import NovelForm.NovelForm.domain.alert.domain.Alert;
import NovelForm.NovelForm.domain.alert.domain.AlertType;
import NovelForm.NovelForm.domain.alert.dto.AlertInfo;
import NovelForm.NovelForm.domain.alert.exception.WrongAlertAccessException;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.repository.AlertRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class AlertServiceTest {

    @Autowired
    MemberRepository memberRepository;


    @Autowired
    AlertRepository alertRepository;


    @Autowired
    EntityManager em;


    /**
     * 알림 조회
     */
    @Test
    @DisplayName("알림 조회 서비스 테스트")
    void getAlert(){

        // given
        // 알림 생성
        Member member = new Member("test@test.com", "123123123", "test nini", Gender.MALE, LoginType.USER, 34);

        Alert alert = new Alert("test Alert", "http://localhost:8080/novel/35", AlertType.AUTHOR);
        alert.addMember(member);

        memberRepository.save(member);
        alertRepository.save(alert);


        // when
        List<AlertInfo> alertByMember = alertRepository.findAlertByMember(member);

        for (AlertInfo alert1 : alertByMember) {
            System.out.println("alert1 = " + alert1);
        }

        // then
        for (AlertInfo alert1 : alertByMember) {
            Assertions.assertThat(alert1.getAlertType()).isEqualTo(alert.getAlertType());
            Assertions.assertThat(alert1.getTitle()).isEqualTo(alert.getTitle());
            Assertions.assertThat(alert1.getUrl()).isEqualTo(alert.getUrl());
        }

    }


    /**
     * 알림 삭제
     */
    @Test
    @DisplayName("알림 삭제 서비스 테스트")
    void deleteAlert() throws WrongAlertAccessException {
        // given
        // 알림 생성
        Member member = new Member("test@test.com", "123123123", "test nini", Gender.MALE, LoginType.USER, 34);

        Alert alert = new Alert("test Alert", "http://localhost:8080/novel/35", AlertType.AUTHOR);
        alert.addMember(member);

        memberRepository.save(member);
        alertRepository.save(alert);


        em.flush();
        em.clear();


        // when
        // 알림을 찾아서 삭제
        Alert findAlert = alertRepository.findAlertByIdAndMemberId(alert.getId(), member.getId());

        if (findAlert == null){
            throw new WrongAlertAccessException("권한 없는 사용자의 접근");
        }

        alertRepository.delete(findAlert);

        
        // then
        // 더 이상 검색할 수 없다.
        Assertions.assertThat(alertRepository.findAlertByIdAndMemberId(member.getId(), alert.getId())).isNull();
    }


    /**
     * 알림 읽기 처리
     */
    @Test
    @DisplayName("알림 읽기 처리 서비스 테스트")
    void readAlert() throws WrongAlertAccessException {

        // given
        Member member = new Member("test@test.com", "123123123", "test nini", Gender.MALE, LoginType.USER, 34);

        Alert alert = new Alert("test Alert", "http://localhost:8080/novel/35", AlertType.AUTHOR);
        alert.addMember(member);

        memberRepository.save(member);
        alertRepository.save(alert);


        em.flush();
        em.clear();


        // when
        // 알림을 찾아서 읽기 처리
        Alert findAlert = alertRepository.findAlertByIdAndMemberId(alert.getId(), member.getId());

        if (findAlert == null){
            throw new WrongAlertAccessException("권한 없는 사용자의 접근");
        }

        findAlert.read();


        em.flush();
        em.clear();

        // then
        // 읽기 처리된 알림은 readCheck가 1로 변경 되어야 한다.
        Assertions.assertThat(alertRepository.findAlertByIdAndMemberId(alert.getId(), member.getId()).getReadCheck()).isEqualTo(1);

    }



}