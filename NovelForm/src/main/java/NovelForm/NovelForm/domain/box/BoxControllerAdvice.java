package NovelForm.NovelForm.domain.box;


import NovelForm.NovelForm.domain.box.exception.*;
import NovelForm.NovelForm.global.BaseResponse;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice("NovelForm.NovelForm.domain.box")
public class BoxControllerAdvice {


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
     * Login Interceptor에서 에러가 생긴 경우 호출 된다.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(LoginInterceptorException.class)
    public BaseResponse loginInterceptorExHandler(Exception e){
        log.error("[box exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, null, e.getMessage());
    }


    /**
     * 서버의 로직에서 문제가 생긴 경우 밑의 핸들러가 호출된다.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public BaseResponse exHandler(Exception e){
        log.error("[member exception handler] ex", e);
        return new BaseResponse(INTERNAL_SERVER_ERROR, null,"서버 내부 오류");
    }


    /**
     *  없는 작품에 접근하는 경우 밑의 핸들러가 호출
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NoSuchBoxItemException.class)
    public BaseResponse noSuchElementExHandler(Exception e){
        log.error("[box exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(),"잘못된 작품 번호를 전달");
    }


    /**
     *  없는 사용자에 대한 접근
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongMemberException.class)
    public BaseResponse wrongMemberExHandler(Exception e){
        log.error("[box exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "없는 사용자에 대한 접근");
    }

    /**
     *  없는 보관함에 대한 접근
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongBoxException.class)
    public BaseResponse wrongBoxExHandler(Exception e){
        log.error("[box exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "없는 보관함에 대한 접근");
    }

    /**
     *  보관함에 대한 권한이 없는 사용자의 접근
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongAccessBoxException.class)
    public BaseResponse wrongAccessBoxExHandler(Exception e){
        log.error("[box exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "보관함에 대한 권한이 없는 사용자의 접근");
    }

}
