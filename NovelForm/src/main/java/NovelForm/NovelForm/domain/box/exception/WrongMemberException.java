package NovelForm.NovelForm.domain.box.exception;

public class WrongMemberException extends Exception{
    public WrongMemberException() {
        super();
    }

    public WrongMemberException(String message) {
        super(message);
    }

    public WrongMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongMemberException(Throwable cause) {
        super(cause);
    }

    protected WrongMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
