package model.constraints;

import model.Operation;

import static model.Operation.NONE;
import static model.Operation.extractOperator;

public class Constraint {

    public static ParsedConstraint parseJSON(String json) {
        String raw = json.substring(1, json.length());

        Operation op = extractOperator(raw);
        String name = raw.substring(0, raw.indexOf(op.getStringValue()));
        String version = op.equals(NONE) ? null :  raw.substring(raw.indexOf(op.getStringValue()), raw.length());

        return new ParsedConstraint(name, version, op);
    }

}
