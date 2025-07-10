package fr.maxlego08.items.api.runes.exceptions;

public class RuneAppliedException extends RuneException {

    public RuneAppliedException() {
    }

    public RuneAppliedException(String message) {
        super(message);
    }

    public RuneAppliedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuneAppliedException(Throwable cause) {
        super(cause);
    }

    public RuneAppliedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
