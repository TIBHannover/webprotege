package edu.stanford.bmir.protege.web.shared.project;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 *
 * Date 30.08.2022
 */
public class PushProjectsToGithubAction implements Action<PushProjectsToGithubResult> {

    private ProjectId projectId;

    private PushProjectsToGithubAction() {
    }

    public PushProjectsToGithubAction(ProjectId projectId) {
        this.projectId = projectId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }
}
