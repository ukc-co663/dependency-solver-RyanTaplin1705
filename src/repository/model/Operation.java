package repository.model;

public enum Operation {
    GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO, EQUAL_TO, NONE;

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
}
