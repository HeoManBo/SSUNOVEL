package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.exception.LoginInterceptorException;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice("NovelForm.NovelForm.domain.member")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberControllerAdvice {

    /**
     * 문법 오류에 따른 예외지만, 현재 @Requestbody에서 문법 오류가 걸리지 않아
     * BindingResult.hasErrors()로 에러 사항이 잡히면 밑의 핸들러가 호출되도록 처리했다.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse illegalExHandler(IllegalArgumentException e){
        log.error("[member exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "필드 에러");
    }


    /**
     * @NotEmpty에 걸릴 경우 밑의 핸들러가 호출된다,
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(UnexpectedTypeException.class)
    public BaseResponse unexpectExHandler(UnexpectedTypeException e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, null,"Type으로 기대되지 않은 값이 왔습니다.");
    }



    /**
     * 회원 중복 생성 시 호출될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MemberDuplicateException.class)
    public BaseResponse memberDuplicateExHandler(MemberDuplicateException e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getErrorFieldMap(), "해당 필드 값으로 이미 회원 가입이 되어 있습니다.");
    }


    /**
     * 잘못된 회원 번호가 들어온 경우 호출될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongMemberException.class)
    public BaseResponse wrongMemberExHandler(WrongMemberException e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "해당 회원이 없습니다.");
    }


    /**
     *  회원 가입이 안 되어있는 상태에서 로그인시 호출 될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongLoginException.class)
    public BaseResponse wrongLoginExHandler(Exception e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "회원 가입이 안 되어 있습니다.");
    }


    /**
     * 출생연도를 파싱할 수 없을 때 호출 될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public BaseResponse dateTimeParseExHandler(Exception e){
        log.error("[member seception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "출생연도 파싱 오류 format: 2023-01-01");
    }

}
