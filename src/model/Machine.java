package model;

import model.exceptions.InvalidStateException;
import model.instructions.Instruction;
import org.json.JSONArray;
import repository.model.Dependency;

import java.util.List;

import static model.exceptions.InvalidStateException.REASON_CONFLICT;
import static util.Printer.stringFormat;

public class Machine {

    private State state;

    public Machine(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void printHistory() {
        System.out.println(new JSONArray(stringFormat(state.history)));
    }

    public void processInstructions(List<Instruction> instructions) throws Exception {
        for (Instruction instruction : instructions) {
            processInstruction(instruction);
        }
    }

    private void processInstruction(Instruction instruction) throws Exception {
        if (state.state.isEmpty()) {
            instruction.run(state);
        } else {
            List<Dependency> conflicts = state.conflicts(instruction.getName(), instruction.getVersion());
            if (!conflicts.isEmpty() && !state.canUninstall(conflicts)) throw new InvalidStateException(REASON_CONFLICT);
            else {
                instruction.run(state);
            }
        }
    }
}
