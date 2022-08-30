package edu.stanford.bmir.protege.web.client.projectmanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import edu.stanford.bmir.protege.web.client.download.DownloadSettingsDialog;
import edu.stanford.bmir.protege.web.client.download.ProjectRevisionDownloader;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;
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
    public void handleCommitProjectRequest(ProjectId projectId) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {

                //TODO: implement method that runs servlet to commit updated ontology. Similar to implementation of
                // a servlet that loads ontology into Webprotege as a project.

//                DownloadSettingsDialog.showDialog(extension -> doDownload(projectId, extension));
            }
        });
    }

//    @Override
//    public void handleCommitRequest(final ProjectId projectId) {
//        GWT.runAsync(new RunAsyncCallback() {
//            @Override
//            public void onFailure(Throwable reason) {
//            }
//
//            @Override
//            public void onSuccess() {
//                DownloadSettingsDialog.showDialog(extension -> doDownload(projectId, extension));
//            }
//        });
//    }

    private void doDownload(ProjectId projectId, DownloadFormatExtension extension) {
        RevisionNumber head = RevisionNumber.getHeadRevisionNumber();
        ProjectRevisionDownloader downloader = new ProjectRevisionDownloader(projectId, head, extension);
        downloader.download();
    }


}