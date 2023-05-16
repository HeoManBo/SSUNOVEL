package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;
import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.repository.MemberRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Long createMember(CreateMemberRequest createMemberRequest) throws Exception {

        Map<String, String> errorFieldMap = new HashMap<>();

        // 이미 같은 이메일로 회원 가입이 되어 있다면, 에러
        if(memberRepository.findByEmail(createMemberRequest.getEmail()) != null){
            errorFieldMap.put("email", createMemberRequest.getEmail());
        }

        // 이미 같은 닉네임으로 회원 가입이 되어 있다면, 에러
        if(memberRepository.findByNickname(createMemberRequest.getNickname()) != null){
            errorFieldMap.put("nickname", createMemberRequest.getNickname());
        }

        if(!errorFieldMap.isEmpty()){
            throw new MemberDuplicateException(errorFieldMap);
        }


        // 출생 연도로 나이를 구하기
        LocalDate birth = LocalDate.parse(createMemberRequest.getAge());
        LocalDate nowDate = LocalDate.now();

        Integer age = (int) ChronoUnit.YEARS.between(birth, nowDate);


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
                .age(age)
                .build();

        log.info("member = {}", member);

        // 회원 저장...
        memberRepository.save(member);
        //log.info("save member id = {}", member.getId());
        return member.getId();
    }

    /**
     *  로그인 서비스 로직
     *  
     *  해당 이메일로 회원 가입이 되어있는지 체크
     *  비밀번호가 일치하는지 체크
     */
    public Long loginMember(LoginMemberRequest loginMemberRequest) throws WrongLoginException {


        Member findMember = memberRepository.findByEmail(loginMemberRequest.getEmail());
        log.info("member={}", findMember);

        // 이미 같은 이메일로 회원 가입이 되어 있지 않다면, 에러
        if(findMember == null){
            throw new WrongLoginException("해당 이메일로 회원 가입이 되어 있지 않습니다.");
        }

        // 비밀번호가 다르다면, 에러
        if(!encoder.matches(loginMemberRequest.getPassword(), findMember.getPassword())){
            throw new WrongLoginException("비밀번호가 다릅니다.");
        }
        

        return findMember.getId();
    }

    /**
     *
     * @param memberId 에 대응되는 멤버가 DB에 존재하는지 확인함.
     * @return 존재하면 멤버 객체, 없다면 null을 반환함
     */
    public Member isPresentMember(Long memberId){
        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElse(null);
    }
}
