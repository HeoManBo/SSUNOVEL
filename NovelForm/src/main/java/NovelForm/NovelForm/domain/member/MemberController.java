package NovelForm.NovelForm.domain.member;


import NovelForm.NovelForm.domain.member.dto.CreateMemberRequest;
import NovelForm.NovelForm.domain.member.dto.LoginMemberRequest;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import NovelForm.NovelForm.global.SessionConst;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


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
     *  다른 에러에 대한 다른 메시지가 나오게 해야 한다 (미 구현)
     */
    @Operation(summary = "회원 가입 메서드", description = "회원 가입 메서드입니다.")
    @PostMapping("/create")
    public BaseResponse<Long> createMember(
            @Validated @RequestBody CreateMemberRequest createMemberRequest,
            BindingResult bindingResult,
            HttpServletRequest request
            ) throws Exception {


        // field 에러 체크
        if(bindingResult.hasFieldErrors()){
            String message = ErrorResultCreater.objectErrorToJson(bindingResult.getFieldErrors());
            throw new IllegalArgumentException(message);
        }

        Long id = memberService.createMember(createMemberRequest);


        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, id);

        return new BaseResponse<Long>(HttpStatus.OK, id, "생성 성공");
    }


    /**
     *  로그인 메소드
     */
    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @PostMapping("/login")
    public BaseResponse loginMember(
            @Validated @RequestBody LoginMemberRequest loginMemberRequest,
            BindingResult bindingResult,
            HttpServletRequest request
    ) throws JsonProcessingException, WrongLoginException {

        // field 에러 체크
        if(bindingResult.hasFieldErrors()){
            String message = ErrorResultCreater.objectErrorToJson(bindingResult.getFieldErrors());
            throw new IllegalArgumentException(message);
        }

        Long id = memberService.loginMember(loginMemberRequest);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, id);

        return new BaseResponse<Long>(HttpStatus.OK, id, "생성 성공");
    }

}
