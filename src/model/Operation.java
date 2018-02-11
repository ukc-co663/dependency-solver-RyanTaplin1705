package model;

import model.exceptions.InvalidParsingException;

public enum Operation {

    GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN("<"),  LESS_THAN_OR_EQUAL_TO("<="),
    EQUAL_TO("="), NONE("");

    private String stringValue;

    Operation(String value) {
        stringValue = value;
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

    public String getStringValue() {
        return stringValue;
    }

    public static Operation extractOperator(String s) throws InvalidParsingException {
        for(Operation o : Operation.values()) {
            if (s.contains(o.getStringValue())) return o;
        }
        throw new InvalidParsingException("Invalid operator {" + s + "} is being processed.");
    }
}
