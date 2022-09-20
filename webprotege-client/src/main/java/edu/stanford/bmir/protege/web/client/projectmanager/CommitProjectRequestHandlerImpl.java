package edu.stanford.bmir.protege.web.client.projectmanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import edu.stanford.bmir.protege.web.client.download.ProjectRevisionDownloader;
import edu.stanford.bmir.protege.web.client.github.CommitSettingsDialog;
import edu.stanford.bmir.protege.web.client.github.ProjectRevisionCommit;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import javax.inject.Inject;

/**
 * Author Nenad Krdzavac <br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 25.08.2022.<br>
 *
 */
public class CommitProjectRequestHandlerImpl implements CommitProjectRequestHandler {

    @Inject
    public CommitProjectRequestHandlerImpl() {

    }
    @Override
    public void handleCommitProjectRequest(AvailableProject project, String token) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                CommitSettingsDialog.showDialog(extension -> doCommit(project.getProjectId(), extension), project.getRepoURI(), token);
            }
        });
    }

    private void doCommit(ProjectId projectId, GithubFormatExtension extension) {
        RevisionNumber head = RevisionNumber.getHeadRevisionNumber();
        ProjectRevisionCommit commit = new ProjectRevisionCommit(projectId, head, extension);
        commit.commit();
    }


}