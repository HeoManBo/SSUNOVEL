package NovelForm.NovelForm.domain.like.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DuplicateAddLikeException extends Exception{

    Map<String, Long> errorFieldMap = new HashMap<>();



    public DuplicateAddLikeException() {
        super();
    }

    public DuplicateAddLikeException(Map<String, Long> errorFieldMap){
        super();
        this.errorFieldMap = errorFieldMap;


    }

    public DuplicateAddLikeException(String message) {
        super(message);
    }

    public DuplicateAddLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateAddLikeException(Throwable cause) {
        super(cause);
    }

    protected DuplicateAddLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
