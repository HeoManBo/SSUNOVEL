package NovelForm.NovelForm.domain.like.exception;

public class WrongBoxException extends Exception{
    public WrongBoxException() {
        super();
    }

    public WrongBoxException(String message) {
        super(message);
    }

    public WrongBoxException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongBoxException(Throwable cause) {
        super(cause);
    }

    protected WrongBoxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
