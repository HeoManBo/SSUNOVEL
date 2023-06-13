package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.*;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoxRepository boxRepository;
    private final FavoriteBoxRepository favoriteBoxRepository;
    private final FavoriteNovelRepository favoriteNovelRepository;
    private final FavoriteAuthorRepository favoriteAuthorRepository;
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final AuthorRepository authorRepository;
    private final NovelRepository novelRepository;
    private final CommunityPostRepository communityPostRepository;

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


    /**
     * 회원 수정 서비스 로직
     *
     * @param memberId
     * @param updateMemberRequest
     * @return
     */
    public String updateMember(Long memberId, UpdateMemberRequest updateMemberRequest) throws Exception {

        Member member = checkMember(memberId);


        if (memberRepository.findByNickname(updateMemberRequest.getNickname()) != null){
            Map<String, String> errorFieldMap = new HashMap<>();
            errorFieldMap.put("nickname", updateMemberRequest.getNickname());
            throw new MemberDuplicateException(errorFieldMap);
        }

        LocalDate birth = LocalDate.parse(updateMemberRequest.getAge());
        LocalDate nowDate = LocalDate.now();

        Integer age = (int) ChronoUnit.YEARS.between(birth, nowDate);

        String password = encoder.encode(updateMemberRequest.getPassword());

        member.updateMember(password,
                updateMemberRequest.getNickname(),
                updateMemberRequest.getGender(),
                age);

        return "수정 완료";

    }

    /**
     * 회원이 작성한 모든 내용을 삭제...
     *
     * 멤버 하나 하고만 연관 관계를 맺는 즐겨찾기나 좋아요 같은 경우에는 Cascade.REMOVE로 삭제 처리를 하되,
     * 여러 멤버와 연관을 맺을 수 있는 보관함이나 리뷰는 직접 bulk연산으로 삭제 처리 했다.
     * 다만, 여기서 보관함 삭제 시, 외래키로 연결된 보관함 즐겨찾기
     *
     * @param memberId
     * @return
     */
    public String deleteMember(Long memberId) throws WrongMemberException {

        Member member = checkMember(memberId);


        // 삭제 부분은 bulk 연산으로 진행했는데,
        // delete 문은 조건에 맞지 않으면 삭제가 안되고 끝이기 때문에 별도의 예외처리를 하지 않았다.
        
        // 리뷰 먼저 삭제
        reviewRepository.bulkDeleteReviewByMember(member);

        // 보관함 즐겨찾기 삭제
        favoriteBoxRepository.deleteAllByMember(member);

        // 보관함 삭제
        boxRepository.bulkDeleteBoxByMember(member);

        // 마지막으로 멤버 삭제
        memberRepository.delete(member);

        return "삭제 완료";
    }


    /**
     * 이메일 중복 체크용 서비스
     *
     * @param check
     * @return
     */
    public String checkEmail(String check) throws MemberDuplicateException {

        Map<String, String> errorFieldMap = new HashMap<>();

        // 헤당 메일로 사용자를 찾을 수 있는가?
        if(memberRepository.findByEmail(check) != null){
            errorFieldMap.put("email", check);
        }

        if(!errorFieldMap.isEmpty()){
            throw new MemberDuplicateException(errorFieldMap);
        }

        return "사용 가능";
    }


    /**
     * 해당 회원이 작성한 글 목록 가져오기
     *
     * @param memberId
     * @param page
     */
    public MemberPostResponse getMemberPost(Long memberId, Integer page) throws WrongMemberException {

        // 사용자 확인
        Member member = checkMember(memberId);

        // 작성글 가져오기
        // 페이징은 10개 기준으로 하기
        // 페이징을 하니까 전체 작성글 개수는 따로 가져와야 한다.
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());


        List<CommunityPost> memberPostList = communityPostRepository.findPostByMember(member, pageRequest).getContent();

        List<PostDto> postDtoList = memberPostList.stream().map(cp ->
                new PostDto(cp.getId(), cp.getTitle(), cp.getMember().getNickname(), cp.getCreate_at(), cp.getComments().size())).toList();

        Integer postCnt = communityPostRepository.findPostCountByMember(member);
        MemberPostResponse memberPostResponse = new MemberPostResponse(postCnt, postDtoList);

        return memberPostResponse;
    }


    /**
     * 사용자가 작성한 리뷰 가져오기
     *
     * @param memberId
     * @param page
     * @return
     */
    public MemberReviewResponse getMemberReview(Long memberId, Integer page) throws WrongMemberException {

        Member member = checkMember(memberId);

        // 작성 리뷰 가져오기
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());
        List<MemberReviewInfo> memberReviewInfoList = reviewRepository.findMemberReviewByMember(member, pageRequest).getContent();
        Integer reviewCnt = reviewRepository.countReviewByMember(member);

        MemberReviewResponse memberReviewResponse = new MemberReviewResponse(reviewCnt, memberReviewInfoList);

        return memberReviewResponse;

    }



    private Member checkMember(Long memberId) throws WrongMemberException {
        Member member;
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 회원 번호 : " + memberId);
        }

        member = optionalMember.get();
        return member;
    }


    /**
     * 사용자가 생성한 모든 보관함을 가저오기
     *
     * @param memberId
     * @param page
     * @return
     */
    public MemberBoxResponse getMemberBox(Long memberId, Integer page) throws WrongMemberException {
        Member member = checkMember(memberId);

        
        
        // 생성한 보관함 가져오기
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());
        List<MemberBoxInfo> memberBoxInfoList = boxRepository.findMemberBoxByMember(member, pageRequest).getContent();
        Integer boxCnt = boxRepository.countBoxByMember(member);

        MemberBoxResponse memberBoxResponse = new MemberBoxResponse(boxCnt, memberBoxInfoList);

        return memberBoxResponse;
    }

    /**
     * 사용자가 즐겨찾기로 등록한 작가 목록 가져오기
     *
     * @param memberId
     * @param page
     * @return
     */
    public MemberFavoriteAuthorResponse getMemberFavoriteAuthor(Long memberId, Integer page) throws WrongMemberException {

        Member member = checkMember(memberId);


        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());
        List<Author> authorList = authorRepository.findAuthorsByMemberFavorite(member, pageRequest).getContent();
        List<MemberFavoriteAuthorInfo> memberFavoriteAuthorInfoList = new ArrayList<>();

        for (Author author : authorList) {
            // Comparator의 람다식
            // 식의 결과가 양수이면 자리를 바꾸고, 음수이면 그대로 유지하게 된다.
            // 뒤에 있는 값이 크면 앞으로 오게 되기에 내림차순 정렬이 가능하다.
            author.getNovels().sort((a, b) -> b.getDownload_cnt() - a.getDownload_cnt());

            Novel mostNovel = author.getNovels().get(0);

            log.info("novel: {}", mostNovel);



            MemberFavoriteAuthorInfo memberFavoriteAuthorInfo = new MemberFavoriteAuthorInfo(
                    author.getId(),
                    author.getName(),
                    mostNovel.getCover_image(),
                    mostNovel.getTitle());

            memberFavoriteAuthorInfoList.add(memberFavoriteAuthorInfo);
        }

        Integer favoriteAuthorCnt = favoriteAuthorRepository.countFavoriteAuthorByMember(member);

        MemberFavoriteAuthorResponse memberFavoriteAuthorResponse =
                new MemberFavoriteAuthorResponse(favoriteAuthorCnt, memberFavoriteAuthorInfoList);

        return memberFavoriteAuthorResponse;
    }

    /**
     * 즐겨찾기로 등록한 보관함 목록 가져오기
     *
     * @param memberId
     * @param page
     * @return
     */
    public MemberFavoriteBoxResponse getMemberFavoriteBox(Long memberId, Integer page) throws WrongMemberException {

        Member member = checkMember(memberId);

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());
        List<MemberBoxInfo> memberFavoriteBoxList = favoriteBoxRepository.findMemberFavoriteBoxByMember(member, pageRequest).getContent();
        Integer boxCnt = favoriteBoxRepository.countFavoriteBoxByMember(member);

        MemberFavoriteBoxResponse memberFavoriteBoxResponse = new MemberFavoriteBoxResponse(boxCnt, memberFavoriteBoxList);

        return memberFavoriteBoxResponse;
    }

    /**
     * 즐겨찾기로 등록한 소설 목록 가져오기
     *
     * @param memberId
     * @param page
     * @return
     */
    public MemberFavoriteNovelResponse getMemberFavoriteNovel(Long memberId, Integer page) throws WrongMemberException {

        Member member = checkMember(memberId);

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("update_at").descending());
        List<MemberFavoriteNovelInfo> novelInfoList = favoriteNovelRepository.findFavoriteNovelInfoByMember(member, pageRequest).getContent();
        Integer novelCnt = favoriteNovelRepository.countFavoriteNovelByMember(member);

        MemberFavoriteNovelResponse memberFavoriteNovelResponse = new MemberFavoriteNovelResponse(novelCnt, novelInfoList);

        return memberFavoriteNovelResponse;
    }


    /**
     * 사용자 정보 가져오기
     *
     * @param memberId
     * @return
     */
    public MemberInfoResponse getMemberInfo(Long memberId) throws WrongMemberException {

        Member member = checkMember(memberId);

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                member.getEmail(),
                member.getNickname(),
                member.getGender(),
                member.getAge()
        );

        return memberInfoResponse;
    }
}
