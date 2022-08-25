package edu.stanford.bmir.protege.web.client.projectmanager;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;

import edu.stanford.bmir.protege.web.shared.project.CommitProjectsToGithubAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;


import javax.inject.Inject;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu<br>
 * Stanford University<br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 24.09.2022.
 */
public class GithubManagerRequestHandlerImpl implements GithubManagerRequestHandler {

    private final DispatchServiceManager dispatchServiceManager;

    @Inject
    public GithubManagerRequestHandlerImpl(DispatchServiceManager dispatchServiceManager) {
        this.dispatchServiceManager = dispatchServiceManager;
    }

    @Override
    public void handleCommitProjectToGithub(ProjectId projectId) {

        dispatchServiceManager.execute(new CommitProjectsToGithubAction(projectId), result -> {});
    }

    @Override
    public void handlePushProjectToGithub(ProjectId projectId) {

    }

//    @Override
//    public void handleMoveProjectToTrash(final ProjectId projectId) {
//        dispatchServiceManager.execute(new MoveProjectsToTrashAction(projectId), result -> {});
//    }
//
//    @Override
//    public void handleRemoveProjectFromTrash(final ProjectId projectId) {
//        dispatchServiceManager.execute(new RemoveProjectFromTrashAction(projectId), result -> {});
//    }
}
