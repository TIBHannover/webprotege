package eu.tib.protege.github.commands;

import eu.tib.protege.github.commands.impl.Output;

public interface GitCommandsService {

    Output gitCloneGitHub(String token, String repoOwnerName, String repoName, String path);

    Output gitCloneGitlab(String userOrTokenType, String token, String gitlabInstance, String instancePath, String localPath);

    Output gitCheckout(String path, String branch);

    Output gitCheckoutNewBranch(String path, String branch);

    Output gitAddAll(String path);

    Output gitCommit(String path, String message);

    Output gitPush(String path, String branch);
}
