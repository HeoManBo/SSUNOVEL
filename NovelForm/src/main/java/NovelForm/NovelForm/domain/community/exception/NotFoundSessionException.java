package NovelForm.NovelForm.domain.community.exception;

public class NotFoundSessionException extends Exception {

    public NotFoundSessionException() {
    }

    public NotFoundSessionException(String message) {
        super(message);
    }

    public NotFoundSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundSessionException(Throwable cause) {
        super(cause);
    }

    public NotFoundSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
