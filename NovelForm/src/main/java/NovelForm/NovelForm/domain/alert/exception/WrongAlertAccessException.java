package NovelForm.NovelForm.domain.alert.exception;

public class WrongAlertAccessException extends Exception{
    public WrongAlertAccessException() {
        super();
    }

    public WrongAlertAccessException(String message) {
        super(message);
    }

    public WrongAlertAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAlertAccessException(Throwable cause) {
        super(cause);
    }

    protected WrongAlertAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
