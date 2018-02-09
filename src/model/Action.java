package model;

public enum Action {
    INSTALL, UNINSTALL;

    public static Action convert(char c) throws Exception {
        if (c == '+') return Action.INSTALL;
        else if (c == '-') return Action.UNINSTALL;
        else throw new Exception("Invalid command {" + c + "}");
    }
}
