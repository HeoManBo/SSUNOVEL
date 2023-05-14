package NovelForm.NovelForm.global;



import NovelForm.NovelForm.global.exception.LoginInterceptorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(LoginInterceptorException.class)
    public BaseResponse loginInterceptorExHandler(Exception e){
        log.error("[favorite exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, null, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public BaseResponse exHandler(Exception e){
        log.error("[box exception handler] ex", e);
        return new BaseResponse(INTERNAL_SERVER_ERROR, null,"서버 내부 오류");
    }
}
