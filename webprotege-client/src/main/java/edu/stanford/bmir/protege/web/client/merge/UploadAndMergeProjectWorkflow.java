package edu.stanford.bmir.protege.web.client.merge;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.projectsettings.GeneralSettingsView;
import edu.stanford.bmir.protege.web.client.upload.UploadAndCloneDialogController;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.csv.DocumentId;
import edu.stanford.bmir.protege.web.client.library.dlg.WebProtegeDialog;
import edu.stanford.bmir.protege.web.client.upload.UploadFileResultHandler;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26/01/15
 */
public class UploadAndMergeProjectWorkflow {

    @Nonnull
    private final MergeUploadedProjectWorkflow mergeWorkflow;

    @Nonnull
    private final GeneralSettingsView generalSettingsView;

    @Nonnull
    private final LoggedInUserProvider loggedInUserProvider;



    @Inject
    public UploadAndMergeProjectWorkflow(@Nonnull MergeUploadedProjectWorkflow mergeWorkflow, @Nonnull GeneralSettingsView generalSettingsView,@Nonnull LoggedInUserProvider loggedInUserProvider) {
        this.mergeWorkflow = checkNotNull(mergeWorkflow);
        this.loggedInUserProvider = checkNotNull(loggedInUserProvider);
        this.generalSettingsView = checkNotNull(generalSettingsView);
    }

    public void start(ProjectId projectId) {
        uploadProject(projectId);
    }


    private void uploadProject(final ProjectId projectId) {
        UploadAndCloneDialogController uploadFileDialogController = new UploadAndCloneDialogController("Upload ontologies", loggedInUserProvider.getCurrentUserToken(), loggedInUserProvider.getCurrentUserId().getUserName(), generalSettingsView.getDisplayName(), new UploadFileResultHandler() {
            @Override
            public void handleFileUploaded(DocumentId fileDocumentId) {
                startMergeWorkflow(projectId, fileDocumentId);
            }

            @Override
            public void handleFileUploadFailed(String errorMessage) {
                GWT.log("Upload failed");
            }
        });
        WebProtegeDialog.showDialog(uploadFileDialogController);
    }


    private void startMergeWorkflow(ProjectId projectId, DocumentId documentId) {
        mergeWorkflow.start(projectId, documentId);
    }

}
