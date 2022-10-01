package edu.stanford.bmir.protege.web.client.github;

import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;

import java.nio.file.Path;

public class CommitData {
    GithubFormatExtension gfe;
    String branch;
    String message;

    String path;

    public CommitData(GithubFormatExtension gfe, String branch, String message, String path) {
        this.gfe = gfe;
        this.branch = branch;
        this.message = message;
        this.path = path;
    }

    public GithubFormatExtension getGfe() {
        return gfe;
    }

    public String getBranch() {
        return branch;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() { return path; }
}
