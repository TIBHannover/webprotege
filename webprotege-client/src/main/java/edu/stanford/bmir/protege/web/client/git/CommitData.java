package edu.stanford.bmir.protege.web.client.git;

import edu.stanford.bmir.protege.web.shared.git.CommitFormatExtension;
import edu.stanford.bmir.protege.web.shared.git.CommitFormatExtension;

public class CommitData {
    CommitFormatExtension gfe;
    String branch;

    String newBranch;
    String message;

    String path;

    public CommitData(CommitFormatExtension gfe, String branch, String newBranch, String message, String path) {
        this.gfe = gfe;
        this.branch = branch;
        this.newBranch = newBranch;
        this.message = message;
        this.path = path;
    }

    public CommitFormatExtension getGfe() {
        return gfe;
    }

    public String getBranch() {
        return branch;
    }

    public String getNewBranch() { return newBranch; }

    public String getMessage() {
        return message;
    }

    public String getPath() { return path; }
}
