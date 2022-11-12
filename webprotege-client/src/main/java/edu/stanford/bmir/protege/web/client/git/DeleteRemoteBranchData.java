package edu.stanford.bmir.protege.web.client.git;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public class DeleteRemoteBranchData {
    String branch;

    public DeleteRemoteBranchData(String branch) {
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

}
