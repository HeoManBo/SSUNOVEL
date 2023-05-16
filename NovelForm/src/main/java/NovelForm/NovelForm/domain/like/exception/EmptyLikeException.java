package NovelForm.NovelForm.domain.like.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EmptyLikeException extends Exception{

    private Map<String, Long> errorFieldMap = new HashMap<>();

    public EmptyLikeException() {
        super();
    }

    public EmptyLikeException(Map<String, Long> errorFieldMap){
        super();
        this.errorFieldMap = errorFieldMap;
    }

    public EmptyLikeException(String message) {
        super(message);
    }

    public EmptyLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyLikeException(Throwable cause) {
        super(cause);
    }

    protected EmptyLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
