package NovelForm.NovelForm.domain.like.exception;

public class WrongReviewException extends Exception {
    public WrongReviewException() {
        super();
    }

    public WrongReviewException(String message) {
        super(message);
    }

    public WrongReviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongReviewException(Throwable cause) {
        super(cause);
    }

    protected WrongReviewException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
