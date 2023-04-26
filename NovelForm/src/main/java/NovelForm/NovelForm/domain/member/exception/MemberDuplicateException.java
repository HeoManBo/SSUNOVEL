package NovelForm.NovelForm.domain.member.exception;

public class MemberDuplicateException extends Exception{
    public MemberDuplicateException() {
        super();
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
