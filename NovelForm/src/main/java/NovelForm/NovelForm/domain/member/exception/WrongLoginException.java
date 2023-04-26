package NovelForm.NovelForm.domain.member.exception;

public class WrongLoginException extends Exception{
    public WrongLoginException() {
        super();
    }

    public WrongLoginException(String message) {
        super(message);
    }

    public WrongLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongLoginException(Throwable cause) {
        super(cause);
    }

    protected WrongLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
