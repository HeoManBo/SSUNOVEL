package NovelForm.NovelForm.domain.novel.exception;

public class NoSuchNovelListException extends Exception{
    public NoSuchNovelListException() {
        super();
    }

    public NoSuchNovelListException(String message) {
        super(message);
    }

    public NoSuchNovelListException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchNovelListException(Throwable cause) {
        super(cause);
    }

    protected NoSuchNovelListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
