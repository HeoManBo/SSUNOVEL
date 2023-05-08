package NovelForm.NovelForm.domain.box.exception;

import java.util.NoSuchElementException;

public class NoSuchBoxItemException extends NoSuchElementException {
    public NoSuchBoxItemException() {
        super();
    }

    public NoSuchBoxItemException(String s, Throwable cause) {
        super(s, cause);
    }

    public NoSuchBoxItemException(Throwable cause) {
        super(cause);
    }

    public NoSuchBoxItemException(String s) {
        super(s);
    }
}
