package NovelForm.NovelForm.domain.box.exception;

public class WrongAccessBoxException extends Exception{
    public WrongAccessBoxException() {
        super();
    }

    public WrongAccessBoxException(String message) {
        super(message);
    }

    public WrongAccessBoxException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAccessBoxException(Throwable cause) {
        super(cause);
    }

    protected WrongAccessBoxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
