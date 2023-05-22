package NovelForm.NovelForm.domain.community.exception;

public class NotPostOwner extends Exception{
    public NotPostOwner() {
        super();
    }

    public NotPostOwner(String message) {
        super(message);
    }

    public NotPostOwner(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPostOwner(Throwable cause) {
        super(cause);
    }

    protected NotPostOwner(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
