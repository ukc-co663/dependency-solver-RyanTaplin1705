package model.exceptions;

public class InvalidInstructionException extends Exception {

    private String reason;

    public InvalidInstructionException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public static String REASON_UNINSTALL_CONSTRAINT = "Can't uninstall a constraint.";
}
