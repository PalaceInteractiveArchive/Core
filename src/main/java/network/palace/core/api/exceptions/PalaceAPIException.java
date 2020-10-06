package network.palace.core.api.exceptions;

@SuppressWarnings("unused")
public class PalaceAPIException extends RuntimeException {

    public PalaceAPIException() {
    }

    public PalaceAPIException(String message) {
        super(message);
    }

    public PalaceAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public PalaceAPIException(Throwable cause) {
        super(cause);
    }

    public PalaceAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
