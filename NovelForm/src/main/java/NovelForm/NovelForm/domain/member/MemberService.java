package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    /**
     *  사이트 회원가입 서비스 로직
     *
     *  비밀번호 암호화 수행 (BCryptPasswordEncoder 사용)
     */
    private final PasswordEncoder encoder;
    public Long createMember(CreateMemberRequest createMemberRequest) throws MemberDuplicateException {
        
        // 이미 같은 이메일로 회원 가입이 되어 있다면, 에러
        if(memberRepository.findByEmail(createMemberRequest.getEmail()) != null){
            throw new MemberDuplicateException("이미 같은 이메일로 회원 가입이 되어 있습니다.");
        }

        // 비밀번호는 암호화 처리
        // BCryptPasswordEncoder로 암호화 했다.
        // 해시 값을 이용한 암호화 방식이다. 비교할 때는 match를 사용하면 된다.
        // 생성 시 파라미터로 들어가는 인자는 strength 값으로 암호화의 강도를 나타낸다.
        //      클 수록 강도가 세고 암호화 수행 시 시간이 오래 걸린다.
        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String password = encoder.encode(createMemberRequest.getPassword());

        // 전달 받은 정보로 Member 엔티티 생성
        Member member = Member.builder()
                .email(createMemberRequest.getEmail())
                .password(password)
                .nickname(createMemberRequest.getNickname())
                .gender(createMemberRequest.getGender())
                .loginType(LoginType.USER)
                .build();

        log.info("member = {}", member);

        // 회원 저장...
        memberRepository.save(member);
        //log.info("save member id = {}", member.getId());
        return member.getId();
    }
}
