package model.exceptions;

public class InvalidStateException extends Exception {

    private String reason;

    public InvalidStateException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public static String REASON_CONFLICT = "Can't reach intermediate state without conflict.";
    public static String REASON_INVALID_ACTION = "Can't reach intermediate state without conflict.";
}
