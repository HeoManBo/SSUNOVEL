package NovelForm.NovelForm.domain.member;

import NovelForm.NovelForm.domain.member.exception.LoginInterceptorException;
import NovelForm.NovelForm.domain.member.exception.MemberDuplicateException;
import NovelForm.NovelForm.domain.member.exception.WrongLoginException;
import NovelForm.NovelForm.global.BaseResponse;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice(basePackages = {"NovelForm.NovelForm.domain.member"})
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
     * 서버의 로직에서 문제가 생긴 경우 밑의 핸들러가 호출된다.
     */
//    @ResponseStatus(INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public BaseResponse exHandler(Exception e){
//        log.error("[member exception handler] ex", e);
//        return new BaseResponse(INTERNAL_SERVER_ERROR, null,"서버 내부 오류");
//    }


    /**
     * Login Interceptor에서 에러가 생긴 경우 호출 된다.
     */
//    @ResponseStatus(BAD_REQUEST)
//    @ExceptionHandler(LoginInterceptorException.class)
//    public BaseResponse loginInterceptorExHandler(Exception e){
//        log.error("[member exception handler] ex ", e);
//        return new BaseResponse(BAD_REQUEST, null, e.getMessage());
//    }


    /**
     * 회원 중복 생성 시 호출될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MemberDuplicateException.class)
    public BaseResponse memberDuplicateExHandler(Exception e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, null, e.getMessage());
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
}
