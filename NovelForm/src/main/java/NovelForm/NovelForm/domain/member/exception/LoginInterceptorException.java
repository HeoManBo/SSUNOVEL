package NovelForm.NovelForm.domain.member.exception;


public class LoginInterceptorException extends Exception{

    public LoginInterceptorException() {
        super();
    }

    public LoginInterceptorException(String message) {
        super(message);
    }

    public LoginInterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginInterceptorException(Throwable cause) {
        super(cause);
    }

    protected LoginInterceptorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
