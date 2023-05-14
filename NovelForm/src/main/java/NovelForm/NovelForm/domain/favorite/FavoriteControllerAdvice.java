package NovelForm.NovelForm.domain.favorite;



import NovelForm.NovelForm.domain.favorite.exception.*;
import NovelForm.NovelForm.global.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice("NovelForm.NovelForm.domain.favorite")
public class FavoriteControllerAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse illegalExHandler(IllegalArgumentException e){
        log.error("[favorite exception handler] ex ", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "필드 에러");
    }

    /**
     * Login Interceptor에서 에러가 생긴 경우 호출 된다.
     */
//    @ResponseStatus(BAD_REQUEST)
//    @ExceptionHandler(LoginInterceptorException.class)
//    public BaseResponse loginInterceptorExHandler(Exception e){
//        log.error("[favorite exception handler] ex ", e);
//        return new BaseResponse(BAD_REQUEST, null, e.getMessage());
//    }


    /**
     * 서버의 로직에서 문제가 생긴 경우 밑의 핸들러가 호출된다.
     */
//    @ResponseStatus(INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public BaseResponse exHandler(Exception e){
//        log.error("[favorite exception handler] ex", e);
//        return new BaseResponse(INTERNAL_SERVER_ERROR, null,"서버 내부 오류");
//    }


    /**
     *  즐겨찾기 추가 요청이 중복으로 들어올 경우 호출될 핸들러
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DuplicateFavoriteException.class)
    public BaseResponse duplicateFavoriteExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "이미 등록된 즐겨찾기");
    }


    /**
     *  작가 번호가 잘못 들어온 경우
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongAuthorException.class)
    public BaseResponse wrongAuthorExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 작가 번호");
    }


    /**
     *  소설 번호가 잘못 들어온 경우
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongNovelException.class)
    public BaseResponse wrongNovelExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 소설 번호");
    }



    /**
     *  보관함 번호가 잘못 들어온 경우
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongBoxException.class)
    public BaseResponse wrongBoxExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 보관함 번호");
    }



    /**
     * 회원 번호가 잘못 들어온 경우
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongMemberException.class)
    public BaseResponse wrongMemberExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "잘못된 회원 번호");
    }


    /**
     * 등록하지 않은 즐겨찾기를 취소할 경우
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WrongFavoriteAccessException.class)
    public BaseResponse wrongFavoriteAccessExHandler(Exception e){
        log.error("[favorite exception handler] ex", e);
        return new BaseResponse(BAD_REQUEST, e.getMessage(), "등록하지 않은 즐겨찾기 취소");
    }

}
