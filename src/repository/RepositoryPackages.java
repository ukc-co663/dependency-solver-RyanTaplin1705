package repository;

import model.Operation;
import repository.model.Package;
import util.VersionChecker;

import java.util.LinkedList;

/**
 * This class is a representation of returning multiple packages from the repository.
 */
public class RepositoryPackages {

    private LinkedList<Package> packages;

    public RepositoryPackages(LinkedList<Package> packages) {
        if (packages == null) this.packages = new LinkedList<>();
        else this.packages = packages;
    }

    public Package ofVersion(String version) throws Exception {
        for (Package p : packages) {
            if (VersionChecker.versionEvaluate(p.version, version, Operation.EQUAL_TO)) return p;
        }
        throw new Exception("Package {" + packages.get(0).name + "} of version {" + version + "} does not exist.");
    }

    public LinkedList<Package> ofVersions(Operation op, String version) throws Exception {
        LinkedList<Package> result = new LinkedList<>();
        for (Package p : this.packages) {
            if (op.equals(Operation.NONE) || VersionChecker.versionEvaluate(p.version, version, op)) {
                result.add(p);
            }
        }
        return result;
    }
}
