package NovelForm.NovelForm.domain.alert;


import NovelForm.NovelForm.domain.alert.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AlertControllerAdvice {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongMemberException.class)
    public BaseResponse wrongMemberException(WrongMemberException e){
        log.error("[alert exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "없는 사용자에 대한 접근");
    }
}
