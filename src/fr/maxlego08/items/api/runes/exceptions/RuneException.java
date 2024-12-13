package fr.maxlego08.items.api.runes.exceptions;

public class RuneException extends Exception {

    public RuneException() {
    }

    public RuneException(String message) {
        super(message);
    }

    public RuneException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuneException(Throwable cause) {
        super(cause);
    }

    public RuneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
