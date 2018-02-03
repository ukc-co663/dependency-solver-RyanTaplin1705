package repository.model;

public enum Operation {
    GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO, EQUAL_TO, NONE;

    public boolean run(Dependency dep, String version) {
        String[] splitD = dep.version.split(".");
        String[] splitV = version.split(".");

        switch(this) {
            case GREATER_THAN:
                for (int i = 0; i < splitD.length; i++) {
                    if (Integer.parseInt(splitD[i]) > Integer.parseInt(splitV[i])) return true;
                }
                return false;
            case GREATER_THAN_OR_EQUAL_TO:
                for (int i = 0; i < splitD.length; i++) {
                    if (Integer.parseInt(splitD[i]) >= Integer.parseInt(splitV[i])) return true;
                }
                return false;
            case LESS_THAN:
                for (int i = 0; i < splitD.length; i++) {
                    if (Integer.parseInt(splitD[i]) < Integer.parseInt(splitV[i])) return true;
                }
                return false;
            case LESS_THAN_OR_EQUAL_TO:
                for (int i = 0; i < splitD.length; i++) {
                    if (Integer.parseInt(splitD[i]) <= Integer.parseInt(splitV[i])) return true;
                }
                return false;
            case EQUAL_TO:
                for (int i = 0; i < splitD.length; i++) {
                    if (Integer.parseInt(splitD[i]) == Integer.parseInt(splitV[i])) return true;
                }
                return false;
            default:
                return false;
        }
    }

}
