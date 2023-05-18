package NovelForm.NovelForm.domain.alert;

import NovelForm.NovelForm.domain.alert.domain.Alert;
import NovelForm.NovelForm.domain.alert.dto.AlertInfo;
import NovelForm.NovelForm.domain.alert.dto.GetAlertResponse;
import NovelForm.NovelForm.domain.alert.exception.WrongAlertAccessException;
import NovelForm.NovelForm.domain.alert.exception.WrongAlertException;
import NovelForm.NovelForm.domain.alert.exception.WrongMemberException;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.repository.AlertRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;

    private final MemberRepository memberRepository;


    /**
     * 알림 조회 서비스 로직
     * 
     * @param memberId
     * @return
     * @throws Exception
     */
    public GetAlertResponse getAlert(Long memberId) throws Exception {

        Member member;
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 회원 번호: " + memberId);
        }

        member = optionalMember.get();


        List<AlertInfo> alertList = alertRepository.findAlertByMember(member);


        return new GetAlertResponse(alertList, alertList.size());
    }


    /**
     * 알림 삭제
     *
     * @param memberId
     * @param alertId
     * @return
     */
    public String deleteAlert(Long memberId, Long alertId) throws Exception {

        Member member;
        Alert alert;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 회원 번호: " + memberId);
        }

        if(!optionalAlert.isPresent()){
            throw new WrongAlertException("잘못된 알림 번호: " + alertId);
        }

        member = optionalMember.get();
        alert = optionalAlert.get();


        if(alertRepository.findAlertByIdAndMemberId(alertId, memberId) == null){
            throw new WrongAlertAccessException("권한 없는 사용자의 접근");
        }


        alertRepository.delete(alert);

        return "삭제 완료";
    }


    /**
     * 알림 읽기 처리
     * 
     * @param memberId
     * @param alertId
     * @return
     */
    public String readAlert(Long memberId, Long alertId) throws Exception {

        Member member;
        Alert alert;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Alert> optionalAlert = alertRepository.findById(alertId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 회원 번호: " + memberId);
        }

        if(!optionalAlert.isPresent()){
            throw new WrongAlertException("잘못된 알림 번호: " + alertId);
        }

        member = optionalMember.get();
        alert = optionalAlert.get();


        if(alertRepository.findAlertByIdAndMemberId(alertId, memberId) == null){
            throw new WrongAlertAccessException("권한 없는 사용자의 접근");
        }


        alert.read();
        return "읽기 처리 완료";
    }
}
