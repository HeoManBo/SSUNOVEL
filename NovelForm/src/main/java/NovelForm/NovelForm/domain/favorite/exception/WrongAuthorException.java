package NovelForm.NovelForm.domain.favorite.exception;

public class WrongAuthorException extends Exception{

    public WrongAuthorException() {
        super();
    }

    public WrongAuthorException(String message) {
        super(message);
    }

    public WrongAuthorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAuthorException(Throwable cause) {
        super(cause);
    }

    protected WrongAuthorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
