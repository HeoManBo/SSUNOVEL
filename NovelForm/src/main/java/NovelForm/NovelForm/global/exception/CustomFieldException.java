package NovelForm.NovelForm.global.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomFieldException extends Exception{

    private Map<String, String> exResponse = new HashMap<>();

    public CustomFieldException() {
        super();
    }

    public CustomFieldException(Map<String, String> exResponse) {
        super();
        this.exResponse = exResponse;
    }

    public CustomFieldException(String message) {
        super(message);
    }

    public CustomFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomFieldException(Throwable cause) {
        super(cause);
    }

    protected CustomFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
