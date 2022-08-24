package edu.stanford.bmir.protege.web.client.projectmanager;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;

/**
 * Author: Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu<br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 24.08.2022.
 */
public interface GithubManagerRequestHandler {

    void handleCommitProjectToGithub(ProjectId projectId);

    void handlePushProjectToGithub(ProjectId projectId);
}
