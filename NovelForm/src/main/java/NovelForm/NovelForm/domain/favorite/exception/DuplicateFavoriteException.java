package NovelForm.NovelForm.domain.favorite.exception;

public class DuplicateFavoriteException extends RuntimeException{

    public DuplicateFavoriteException() {
        super();
    }

    public DuplicateFavoriteException(String message) {
        super(message);
    }

    public DuplicateFavoriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFavoriteException(Throwable cause) {
        super(cause);
    }

    protected DuplicateFavoriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
