package model.exceptions;

public class InvalidParsingException extends Exception {

    private String reason;

    public InvalidParsingException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
