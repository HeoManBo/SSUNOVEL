package NovelForm.NovelForm.domain.favorite.exception;


public class WrongNovelException extends Exception{
    public WrongNovelException() {
        super();
    }

    public WrongNovelException(String message) {
        super(message);
    }

    public WrongNovelException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongNovelException(Throwable cause) {
        super(cause);
    }

    protected WrongNovelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
