package NovelForm.NovelForm.domain.alert.exception;

public class WrongAlertException extends Exception{

    public WrongAlertException() {
        super();
    }

    public WrongAlertException(String message) {
        super(message);
    }

    public WrongAlertException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAlertException(Throwable cause) {
        super(cause);
    }

    protected WrongAlertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
