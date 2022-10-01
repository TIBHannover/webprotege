package edu.stanford.bmir.protege.web.client.projectmanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import edu.stanford.bmir.protege.web.client.github.CommitData;
import edu.stanford.bmir.protege.web.client.github.CommitSettingsDialog;
import edu.stanford.bmir.protege.web.client.github.ProjectRevisionCommiter;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Author Nenad Krdzavac <br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 25.08.2022.<br>
 *
 */
public class CommitProjectRequestHandlerImpl implements CommitProjectRequestHandler {

    @Nonnull
    private final LoggedInUserProvider loggedInUserProvider;
    @Inject
    public CommitProjectRequestHandlerImpl(LoggedInUserProvider loggedInUserProvider) {
        this.loggedInUserProvider = loggedInUserProvider;
    }
    @Override
    public void handleCommitProjectRequest(AvailableProject project, String token) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                CommitSettingsDialog.showDialog(commitData -> doCommit(project, commitData, loggedInUserProvider.getCurrentUserToken()), project.getRepoURI(), token);
            }
        });
    }

    private void doCommit(AvailableProject project, CommitData commitData, String token) {
        RevisionNumber head = RevisionNumber.getHeadRevisionNumber();
        ProjectRevisionCommiter commit = new ProjectRevisionCommiter(project, head, commitData);
        commit.commit(token);
    }


}