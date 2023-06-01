package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.community.exception.NoPostException;
import NovelForm.NovelForm.domain.community.exception.NotFoundSessionException;
import NovelForm.NovelForm.domain.community.exception.NotPostOwner;
import NovelForm.NovelForm.domain.community.exception.WrongCommentException;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.exception.NoSuchListElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * CommunityController 예외 처리단입니다.
 */

@RestControllerAdvice("NovelForm.NovelForm.domain.community")
@Slf4j
public class CommunityControllerAdvice {

    /**
     * 존재하지 않는 게시글 번호 조회
     */
    @ExceptionHandler(NoPostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse noPostException(NoPostException ex){
        log.info("[Community Controller ex] ex ", ex);
        return new BaseResponse(HttpStatus.BAD_REQUEST, "존재하지 않는 PostId입니다.", "에러");
    }

    /**
     * 수정이 불가능할 때 에러 발생
     */
    @ExceptionHandler(NotPostOwner.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse notPostOwner(NotPostOwner ex){
        log.info("[Community Controller ex] ex ", ex);
        return new BaseResponse(HttpStatus.FORBIDDEN, "게시글 수정이 불가능합니다.", "에러");
    }

    /**
     * 페이징 번호 오류
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public BaseResponse numberFormatArgumentException(NumberFormatException e){
        log.error("[Community Controller ex] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     *  전달된 페이지 번호에 대응되는 리스트가 존재하지 않을 때 에러처리를 위한 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NoSuchListElement.class)
    public BaseResponse noSuchListElement(NoSuchListElement e){
        log.error("[Community Controller ex] ex", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, "해당 페이지 번호에 대응되는 리스트가 없습니다.",  "에러");
    }

    /**
     * 세션값이 없는 경우 에러 발생
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NotFoundSessionException.class)
    public BaseResponse noSuchListElement(NotFoundSessionException e){
        log.error("[Community Controller ex] ex", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, "세션을 찾을 수 없습니다.",  "에러");
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongCommentException.class)
    public BaseResponse wrongCommentException(WrongCommentException e){
        log.error("[Community Controller ex] ex", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage(),   "에러");
    }

}
