package NovelForm.NovelForm.domain.novel.exception;

public class NotReviewOwner extends Exception{
    public NotReviewOwner() {
        super();
    }

    public NotReviewOwner(String message) {
        super(message);
    }

    public NotReviewOwner(String message, Throwable cause) {
        super(message, cause);
    }

    public NotReviewOwner(Throwable cause) {
        super(cause);
    }

    protected NotReviewOwner(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
