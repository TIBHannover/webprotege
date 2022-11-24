package edu.stanford.bmir.protege.web.client.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.library.dlg.*;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.csv.DocumentId;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.upload.FileUploadResponseAttributes;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 21.11.2022
 */
public class UploadAndCloneDialogController extends WebProtegeOKCancelDialogController<String> {

    private UploadAndCloneDialogForm uploadAndCloneForm = new UploadAndCloneDialogForm();

    public UploadAndCloneDialogController(String title, String token, String userName, String projectName, final UploadFileResultHandler resultHandler) {
        super(title);
        setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<String>() {
            @Override
            public void handleHide(String data, final WebProtegeDialogCloser closer) {

                if(uploadAndCloneForm.uploadSelectorField.getValue()){
                    ProgressMonitor.get().showProgressMonitor("Uploading", "Uploading file");
                    uploadAndCloneForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
                        public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                            ProgressMonitor.get().hideProgressMonitor();
                            GWT.log("Submission of file is complete");
                            Log.info("Submission of file is complete");
                            FileUploadResponse result = new FileUploadResponse(event.getResults());
                            if(result.wasUploadAccepted()) {
                                GWT.log("Successful upload");
                                Log.info("Successful upload");
                                resultHandler.handleFileUploaded(result.getDocumentId());

                            }
                            else {
                                GWT.log("Upload rejected: " + result.getUploadRejectedMessage());
                                Log.info("Upload rejected: " + result.getUploadRejectedMessage());
                                resultHandler.handleFileUploadFailed(result.getUploadRejectedMessage());
                            }
                            closer.hide();

                        }
                    });
                    uploadAndCloneForm.submit();
                } else {

                    uploadAndCloneForm.storeServletValuesInHidden(token, userName, projectName);

                    ProgressMonitor.get().showProgressMonitor("Cloning", "Cloning repository");
                    uploadAndCloneForm.repoFormPanel.addSubmitCompleteHandler(
                            new FormPanel.SubmitCompleteHandler() {
                                public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                                    ProgressMonitor.get().hideProgressMonitor();
                                    GWT.log("Submission of repo file is complete");
                                    Log.info("Submission of repo file is complete");
                                    GWT.log("result: "+event.getResults());
                                    Log.info("result: "+event.getResults());

                                    JSONValue value = JSONParser.parseLenient(event.getResults());
                                    JSONObject object = value.isObject();
                                    DocumentId documentId;
                                    if (object == null)
                                        documentId = new DocumentId("");
                                    else {
                                        JSONValue value1 = object.get(FileUploadResponseAttributes.UPLOAD_FILE_ID.name());
                                        if (value1.isString() == null)
                                            documentId = new DocumentId("");
                                        else
                                            documentId = new DocumentId(value1.isString().stringValue().trim());
                                    }
                                    if(documentId != null)
                                    if(!documentId.getDocumentId().isEmpty()){
                                        GWT.log("Successful clone and upload!");
                                        Log.info("Successful clone and upload!");
                                        resultHandler.handleFileUploaded(documentId);

                                    }
                                    else {
                                        GWT.log("No document found");
                                        Log.info("No document found");
                                        resultHandler.handleFileUploadFailed("Clone failed!");
                                    }
                                    closer.hide();

                                }
                            }
                    );

                    GWT.log("action: "+uploadAndCloneForm.repoFormPanel.getAction());
                    Log.info("action: "+uploadAndCloneForm.repoFormPanel.getAction());
                    GWT.log("encoding: "+uploadAndCloneForm.repoFormPanel.getEncoding());
                    Log.info("encoding: "+uploadAndCloneForm.repoFormPanel.getEncoding());
                    GWT.log("method: "+uploadAndCloneForm.repoFormPanel.getMethod());
                    Log.info("method: "+uploadAndCloneForm.repoFormPanel.getMethod());
                    uploadAndCloneForm.repoFormPanel.submit();
                }


            }
        });
    }


    @Nonnull
    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return uploadAndCloneForm.getInitialFocusable();
    }

    @Override
    public String getData() {
        return uploadAndCloneForm.getFileName();
    }

    @Override
    public Widget getWidget() {
        return uploadAndCloneForm;
    }
}
