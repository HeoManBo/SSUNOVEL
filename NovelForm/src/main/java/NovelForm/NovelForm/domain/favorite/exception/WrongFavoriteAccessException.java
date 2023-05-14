package NovelForm.NovelForm.domain.favorite.exception;

public class WrongFavoriteAccessException extends RuntimeException{

    public WrongFavoriteAccessException() {
        super();
    }

    public WrongFavoriteAccessException(String message) {
        super(message);
    }

    public WrongFavoriteAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongFavoriteAccessException(Throwable cause) {
        super(cause);
    }

    protected WrongFavoriteAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
