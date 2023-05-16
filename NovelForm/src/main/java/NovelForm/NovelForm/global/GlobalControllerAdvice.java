package NovelForm.NovelForm.global;



import NovelForm.NovelForm.global.exception.CustomFieldException;
import NovelForm.NovelForm.global.exception.LoginInterceptorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {


    /**
     * 로그인 인터셉터에서 발생한 예외를 처리하는 부분
     * @param e
     * @return
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(LoginInterceptorException.class)
    public BaseResponse loginInterceptorExHandler(Exception e){
        log.error("[global exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "로그인 필요");
    }


    /**
     * 지정한 예외가 아닌 서비스 로직 등에서 발생한 알 수 없는 예외를 처리하는 핸들러
     * @param e
     * @return
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public BaseResponse exHandler(Exception e){
        log.error("[global exception handler] ex", e);
        return new BaseResponse(INTERNAL_SERVER_ERROR, e.getMessage(),"서버 내부 오류");
    }


    /**
     * BindingResult로 잡는 필드 에러들에 대한 처리를 위한 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CustomFieldException.class)
    public BaseResponse fieldExHandler(CustomFieldException e){
        log.error("[global exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getExResponse(), "필드 에러");
    }


    /**
     *  PathVariable 등에서 잘못된 값을 줄 때 발생한 에러를 처리하는 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseResponse methodArgumentTypeMismatchExHandler(MethodArgumentTypeMismatchException e){
        log.error("[global exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getName(), "컨버팅 오류");
    }




}
