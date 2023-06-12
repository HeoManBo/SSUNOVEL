package NovelForm.NovelForm.domain.member;


import NovelForm.NovelForm.domain.box.FilteringType;
import NovelForm.NovelForm.domain.member.dto.*;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import NovelForm.NovelForm.global.SessionConst;
import NovelForm.NovelForm.global.exception.CustomFieldException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;


@Tag(name = "회원", description = "회원 관련 api입니다.")
@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;


    /**
     *  회원가입 메소드
     *  CreateMemberRequest DTO를 입력으로 받는다.
     *
     *  request body에 대한 type체크는 아직이다.
     *  다른 에러에 대한 다른 메시지가 나오게 해야 한다
     */
    @Operation(summary = "회원 가입", description = "회원 가입 메서드입니다.")
    @PostMapping("/create")
    public BaseResponse<Long> createMember(
            @Validated @RequestBody CreateMemberRequest createMemberRequest,
            BindingResult bindingResult,
            HttpServletRequest request
            ) throws Exception {


        // field 에러 체크
        if(bindingResult.hasFieldErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }




        Long id = memberService.createMember(createMemberRequest);


        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_MEMBER_ID, id);

        return new BaseResponse<Long>(HttpStatus.OK, id, "생성 성공");
    }


    /**
     * 회원 가입 전 이메일 중복 체크
     */
    @Operation(summary = "이메일 중복 체크", description = "회원가입 시 사용될 이메일 중복 체크")
    @GetMapping("/email")
    public BaseResponse<String> checkEmail(
            @Parameter(description = "중복체크할 이메일 주소(test@test.com)", in = ParameterIn.QUERY)
            @RequestParam("check")
            String check) throws Exception {


        boolean matchResult = java.util.regex.Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", check);

        if(!matchResult){
            throw new IllegalArgumentException("이메일 형식 오류: " + check);
        }


        String result = memberService.checkEmail(check);

        return new BaseResponse<>(result);
    }



    /**
     *  로그인 메소드
     */
    @Operation(summary = "로그인", description = "로그인 메서드입니다.")
    @PostMapping("/login")
    public BaseResponse loginMember(
            @Validated @RequestBody LoginMemberRequest loginMemberRequest,
            BindingResult bindingResult,
            HttpServletRequest request
    ) throws Exception {

        // field 에러 체크
        if(bindingResult.hasFieldErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        // 세션이 있는 상황에서 로그인 하려고 하면 기존 세션 삭제하고 다시 로그인 처리
        // 세션 삭제
        if(request.getSession(false) != null){

            // 세션 무효화
            request.getSession(false).invalidate();
        }



        Long id = memberService.loginMember(loginMemberRequest);


        HttpSession session = request.getSession(false);
        if(session == null){
            session = request.getSession();
            session.setAttribute(LOGIN_MEMBER_ID, id);
        }

        return new BaseResponse<Long>(HttpStatus.OK, id, "생성 성공");
    }




    /**
     *  로그 아웃 메서드
     *
     *  세션 해제시키는게 주요 기능
     *  인터셉터에서 이미 세션 체크를 하기 때문에 세션이 없으면 걸러져서 여기까지 오지 않음
     */
    @Operation(summary = "로그 아웃", description = "로그아웃 기능입니다.")
    @GetMapping("/logout")
    public BaseResponse logoutMember(HttpServletRequest request) throws WrongLoginException {

        HttpSession httpSession = request.getSession(false);

        // 세션 삭제
        if(httpSession != null){

            // 세션 무효화
            httpSession.invalidate();
        }

        return new BaseResponse(HttpStatus.OK, null, "로그아웃에 성공했습니다.");
    }


    /**
     *  회원 수정 메서드
     *
     *  이메일을 제외한 나머지 요소들을 변경할 수 있게끔 처리
     */
    @Operation(summary = "회원 수정", description = "회원 수정 기능입니다. 이메일을 제외한 나머지 요소를 변경할 수 있습니다.")
    @PatchMapping("/update")
    public BaseResponse<String> updateMember(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Validated @RequestBody UpdateMemberRequest updateMemberRequest,
            BindingResult bindingResult) throws Exception {

        if(bindingResult.hasFieldErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        String result = memberService.updateMember(memberId, updateMemberRequest);
        return new BaseResponse<>(result);
    }


    /**
     *  회원 삭제 메서드
     *
     *  삭제를 원할 경우 바로 삭제 되도록 처리
     *  회원을 비록한 연관 관계를 맺은 이들을 모두 삭제 처리 한다.
     */
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 기능입니다. 해당 회원이 작성한 모든 내용이 사라집니다.")
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteMember(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId
    ) throws WrongMemberException {


        String result = memberService.deleteMember(memberId);

        return new BaseResponse<>(result);
    }

    /**
     *  회원 정보 가져오기 메서드
     *
     *  닉네임, 이메일, 성별, 나이 정보를 가져온다.
     */
    @Operation(summary = "회원 정보 가져오기", description = "회원 정보 가져오기 기능. 이메일, 닉네임, 성별, 나이 정보")
    @GetMapping("")
    public BaseResponse<MemberInfoResponse> getMemberInfo(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId
    ) throws WrongMemberException {

        MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(memberId);

        return new BaseResponse<>(memberInfoResponse);

    }



    /**
     * 마이페이지
     *
     * 내가 작성하거나 즐겨찾기에 등록한 것들을 가져오기
     * 작성 글
     * 작성 리뷰
     * 생성한 보관함
     * 즐겨찾기한 작가 목록
     * 즐겨찾기한 보관함 목록
     * 즐겨찾기한 소설 목록
     */


    /**
     * 마이페이지 작성 글 가져오기
     */
    @Operation(summary = "작성 글 가져오기", description = "마이페이지에서 가장 먼저 보여질 작성 글 가져오기 (최신 순으로 가져옵니다.)")
    @GetMapping("/mypage/post")
    public BaseResponse<MemberPostResponse> getMemberPost(
        @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
        @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
        @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {

        MemberPostResponse memberPost = memberService.getMemberPost(memberId, page);

        return new BaseResponse<>(memberPost);
    }

    /**
     * 마이페이지 작성 리뷰 가져오기
     */
    @Operation(summary = "작성 리뷰 가져오기", description = "본인이 작성한 리뷰 가져오기")
    @GetMapping("/mypage/review")
    public BaseResponse<MemberReviewResponse> getMemberReview(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
            @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
            @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {


        MemberReviewResponse memberReviewResponse = memberService.getMemberReview(memberId, page);

        return new BaseResponse<>(memberReviewResponse);
    }


    /**
     * 마이페이지 생성 보관함 가져오기
     */
    @Operation(summary = "생성한 보관함 가져오기", description = "본인이 생성한 보관함 가져오기")
    @GetMapping("/mypage/box")
    public BaseResponse<MemberBoxResponse> getMemberBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
            @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
            @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {


        MemberBoxResponse memberBoxResponse = memberService.getMemberBox(memberId, page);

        return new BaseResponse<>(memberBoxResponse);
    }


    /**
     * 마이페이지 즐겨찾기 한 작가 목록 가져오기
     */
    @Operation(summary = "즐겨찾기 한 작가 목록 가져오기", description = "즐겨찾기로 등록한 작가 목록 가져오기")
    @GetMapping("/mypage/favorite/author")
    public BaseResponse<MemberFavoriteAuthorResponse> getMemberFavoriteAuthor(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
            @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
            @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {


        MemberFavoriteAuthorResponse memberFavoriteAuthorResponse = memberService.getMemberFavoriteAuthor(memberId, page);

        return new BaseResponse<>(memberFavoriteAuthorResponse);
    }


    /**
     * 마이페이지 즐겨찾기 한 보관함 목록 가져오기
     */
    @Operation(summary = "즐겨찾기 한 보관함 목록 가져오기", description = "즐겨찾기로 등록한 보관함 목록 가져오기")
    @GetMapping("/mypage/favorite/box")
    public BaseResponse<MemberFavoriteBoxResponse> getMemberFavoriteBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
            @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
            @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {


        MemberFavoriteBoxResponse memberFavoriteBoxResponse = memberService.getMemberFavoriteBox(memberId, page);

        return new BaseResponse<>(memberFavoriteBoxResponse);
    }


    /**
     * 마이페이지 즐겨찾기 한 소설 목록 가져오기
     */
    @Operation(summary = "즐겨찾기 한 소설 목록 가져오기", description = "즐겨찾기로 등록한 소설 목록 가져오기")
    @GetMapping("/mypage/favorite/novel")
    public BaseResponse<MemberFavoriteNovelResponse> getMemberFavoriteNovel(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID) Long memberId,
            @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
            @Validated @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) throws WrongMemberException {


        MemberFavoriteNovelResponse memberFavoriteNovelResponse = memberService.getMemberFavoriteNovel(memberId, page);

        return new BaseResponse<>(memberFavoriteNovelResponse);
    }


}
