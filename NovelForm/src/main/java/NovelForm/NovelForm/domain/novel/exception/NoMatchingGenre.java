package NovelForm.NovelForm.domain.novel.exception;

public class NoMatchingGenre extends Exception{
    public NoMatchingGenre() {
        super();
    }

    public NoMatchingGenre(String message) {
        super(message);
    }

    public NoMatchingGenre(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchingGenre(Throwable cause) {
        super(cause);
    }

    protected NoMatchingGenre(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
