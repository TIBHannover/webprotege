package eu.tib.protege.github.commands;

public interface GitCommandsService {

    void gitCloneGitHub(String token, String repoOwnerName, String repoName, String path);

    public void gitCloneGitlab(String userOrTokenType, String token, String gitlabInstance, String instancePath, String localPath);

    void gitCheckout(String path, String branch);

    void gitAddAll(String path);

    void gitCommit(String path, String message);

    void gitPush(String path, String branch);
}
