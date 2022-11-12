package edu.stanford.bmir.protege.web.client.projectmanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import edu.stanford.bmir.protege.web.client.git.*;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public class DeleteRemoteBranchRequestHandlerImpl implements DeleteRemoteBranchRequestHandler {

    @Nonnull
    private final LoggedInUserProvider loggedInUserProvider;
    @Inject
    public DeleteRemoteBranchRequestHandlerImpl(LoggedInUserProvider loggedInUserProvider) {
        this.loggedInUserProvider = loggedInUserProvider;
    }
    @Override
    public void handleDeleteRemoteBranchRequest(AvailableProject project, String token) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                DeleteBranchSettingsDialog.showDialog(deleteRemoteBranchData -> doDeleteRemoteBranch(project, deleteRemoteBranchData, loggedInUserProvider.getCurrentUserToken()), project.getRepoURI(), token);
            }
        });
    }

    private void doDeleteRemoteBranch(AvailableProject project, DeleteRemoteBranchData deleteRemoteBranchData, String token) {
        RevisionNumber head = RevisionNumber.getHeadRevisionNumber();
        RemoteBranchDeleter switcher = new RemoteBranchDeleter(project, head, deleteRemoteBranchData);
        switcher.switcher(token);
    }

}
