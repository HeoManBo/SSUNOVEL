package NovelForm.NovelForm.domain.community.exception;

public class WrongCommentException extends Exception{
    public WrongCommentException() {
        super();
    }

    public WrongCommentException(String message) {
        super(message);
    }

    public WrongCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongCommentException(Throwable cause) {
        super(cause);
    }

    protected WrongCommentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
