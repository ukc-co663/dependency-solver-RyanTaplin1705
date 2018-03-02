package model.constraints;

import model.Operation;

import static model.Operation.NONE;
import static model.Operation.extractOperator;

public class Constraint {

    public static ParsedConstraint parseJSON(String json) throws Exception {
        String raw = json.substring(1, json.length());

        Operation op = extractOperator(raw);
        String name = op.equals(NONE) ? raw : raw.substring(0, raw.indexOf(op.getStringValue()));
        String version = op.equals(NONE) ? null :  raw.substring(raw.indexOf(op.getStringValue()) + (op.equals(Operation.EQUAL_TO) ? 1 : 0), raw.length());

        return new ParsedConstraint(name, version, op);
    }

}
