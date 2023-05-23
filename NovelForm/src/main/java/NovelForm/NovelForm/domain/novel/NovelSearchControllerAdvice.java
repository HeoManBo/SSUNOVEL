package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.novel.exception.NoSuchNovelListException;
import NovelForm.NovelForm.domain.novel.exception.NotReviewOwner;
import NovelForm.NovelForm.global.BaseResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * NovelController 단 예외처리 API 모음입니다.
 */
@Slf4j
@RestControllerAdvice("NovelForm.NovelForm.domain.novel")
public class NovelSearchControllerAdvice {

    /**
     * 쿼리 파라미터 오류
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse illegalArgumentException(IllegalArgumentException e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "쿼리 파라미터 오류");
    }

    /**
     * 페이징 번호 오류
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public BaseResponse numberFormatArgumentException(NumberFormatException e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "잘못된 값 입니다.");
    }

    /**
     * 반환해줄 소설 리스트가 없는 경우 오류 발생
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchNovelListException.class)
    public BaseResponse noSuchNovelListException(NoSuchNovelListException e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "검색어에 일치하는 소설이 존재하지 않습니다.");
    }

    /**
     * PathvVariable, Pagenum 유효성 체크 예외 발생 추가
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse BadPathOrPagingNum(ConstraintViolationException e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, "메시지 에러확인", "잘못된 pathVariable 값이나, Paging 번호입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonMappingException.class)
    public BaseResponse badJsonValue(JsonMappingException e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "잘못된 JSON 값 전달입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotReviewOwner.class)
    public BaseResponse notReviewOwner(NotReviewOwner e){
        log.error("[novel exception handler] ex ", e);
        return new BaseResponse(HttpStatus.BAD_REQUEST, "해당 리뷰 삭제권한이 없습니다.", "해당 리뷰 삭제권한이 없습니다.");
    }


}
