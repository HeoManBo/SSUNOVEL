package NovelForm.NovelForm.domain.member.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MemberDuplicateException extends Exception{

    private Map<String, String> errorFieldMap = new HashMap<>();

    public MemberDuplicateException() {
        super();
    }

    public MemberDuplicateException(Map<String, String> errorFieldMap){
        super();
        this.errorFieldMap = errorFieldMap;
    }

    public MemberDuplicateException(String message) {
        super(message);
    }

    public MemberDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberDuplicateException(Throwable cause) {
        super(cause);
    }

    protected MemberDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
