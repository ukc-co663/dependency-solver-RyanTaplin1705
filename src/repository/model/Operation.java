package repository.model;

public enum Operation {

    GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN("<"),  LESS_THAN_OR_EQUAL_TO("<="),
    EQUAL_TO("<"), NONE("");

    private String stringValue;

    Operation(String s) {
        stringValue = s;
    }

    public boolean evaluate(int valOne, int valTwo) {
        switch(this) {
            case GREATER_THAN:
                return valTwo > valOne;
            case GREATER_THAN_OR_EQUAL_TO:
                return valTwo >= valOne;
            case LESS_THAN:
                return valTwo < valOne;
            case LESS_THAN_OR_EQUAL_TO:
                return valTwo <= valOne;
            case EQUAL_TO:
                return valTwo == valOne;
            default:
                return true;
        }
    }

    public static Operation extract(String s) {
        //Operation.valueOf(s);
        if (s.contains(">=")) {
            return GREATER_THAN_OR_EQUAL_TO;
        } else if (s.contains("<=")) {
            return LESS_THAN_OR_EQUAL_TO;
        } else if (s.contains("<")) {
            return Operation.LESS_THAN;
        } else if (s.contains(">")) {
            return Operation.GREATER_THAN;
        } else if (s.contains("=")) {
            return Operation.EQUAL_TO;
        } else {
            return Operation.NONE;
        }
    }

    public String getStringValue() {
        return stringValue;
    }
}
