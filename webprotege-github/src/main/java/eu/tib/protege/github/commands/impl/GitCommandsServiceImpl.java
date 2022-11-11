package eu.tib.protege.github.commands.impl;

import eu.tib.protege.github.commands.CommandRunnerService;
import eu.tib.protege.github.commands.GitCommandsService;

public class GitCommandsServiceImpl implements GitCommandsService {
    private final CommandRunnerService commandRunnerService = new CommandRunnerServiceImpl();


    /**
     * git clone https://<PersonalAccessToken>@github.com/<RepoOwnerName>/<RepoName>.git pathToFolder
     *
     * @param token         GitHub PrivateAccessToken
     * @param repoOwnerName repository owner name
     * @param repoName      repository name
     * @param path          path to local directory
     */
    @Override
    public Output gitCloneGitHub(String token, String repoOwnerName, String repoName, String path) {
        var command = "";
        if(token == null || token.isEmpty())
            command = String.format("git clone https://github.com/%s/%s.git %s", repoOwnerName, repoName, path);
        else
            command = String.format("git clone https://%s@github.com/%s/%s.git %s", token, repoOwnerName, repoName, path);
        return commandRunnerService.run(command);
    }
    @Override
    public Output gitCloneGitlab(String userOrTokenType, String passwordOrToken, String gitlabInstance, String instancePath, String localPath){
        var command = "";
        if((userOrTokenType == null || passwordOrToken == null) || (userOrTokenType.isEmpty() || passwordOrToken.isEmpty()))
            command = String.format("git clone https://%s/%s.git %s", gitlabInstance, instancePath, localPath);
        else
            command = String.format("git clone https://%s:%s@%s/%s.git %s", userOrTokenType, passwordOrToken, gitlabInstance, instancePath, localPath);
        return commandRunnerService.run(command);
    }

    /**
     * git checkout -b branch
     *
     * @param path   path to Git local working directory
     * @param branch branch name
     */
    @Override
    public Output gitCheckout(String path, String branch) {
        var command = String.format("git -C %s checkout %s", path, branch);
        return commandRunnerService.run(command);
    }

    @Override
    public Output gitCheckoutNewBranch(String path, String branch) {
        var command = String.format("git -C %s checkout -b %s", path, branch);
        return commandRunnerService.run(command);
    }

    /**
     * git add .
     * adds all new or changed files in Git working directory to the Git staging area
     *
     * @param path path to Git local working directory
     */
    @Override
    public Output gitAddAll(String path) {
        var command = String.format("git -C %s add .", path);
        return commandRunnerService.run(command);
    }

    /**
     * git commit
     *
     * @param path    path to Git local working directory
     * @param message commit message
     */
    @Override
    public Output gitCommit(String path, String message) {
        var command = String.format("git -C %s commit -m \"%s\"", path, message.replace(" ", "%20" ));
        return commandRunnerService.run(command);
    }

    /**
     * git push -f origin branch
     *
     * @param path   path to Git local working directory
     * @param branch branch to be pushed
     */
    @Override
    public Output gitPush(String path, String branch) {
        var command = String.format("git -C %s push -f origin %s", path, branch);
        return commandRunnerService.run(command);
    }
}
