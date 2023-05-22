package NovelForm.NovelForm.global.exception;

public class NoSuchListElement extends Exception{
    public NoSuchListElement() {
        super();
    }

    public NoSuchListElement(String message) {
        super(message);
    }

    public NoSuchListElement(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchListElement(Throwable cause) {
        super(cause);
    }

    protected NoSuchListElement(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
