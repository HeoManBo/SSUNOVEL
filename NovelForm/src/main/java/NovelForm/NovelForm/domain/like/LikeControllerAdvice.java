package NovelForm.NovelForm.domain.like;


import NovelForm.NovelForm.domain.like.exception.DuplicateAddLikeException;
import NovelForm.NovelForm.domain.like.exception.WrongBoxException;
import NovelForm.NovelForm.domain.like.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice("NovelForm.NovelForm.domain.like")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LikeControllerAdvice {


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DuplicateAddLikeException.class)
    public BaseResponse duplicateAddLikeExHandler(DuplicateAddLikeException e){
        log.error("[like exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getErrorFieldMap(), "좋아요 중복 등록");
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongMemberException.class)
    public BaseResponse wrongMemberExHandler(WrongMemberException e){
        log.error("[like exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 사용자");
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongBoxException.class)
    public BaseResponse wrongBoxExHandler(WrongBoxException e){
        log.error("[like exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 보관함");
    }

}
