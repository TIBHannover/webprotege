package edu.stanford.bmir.protege.web.client.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.library.dlg.*;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;

import javax.annotation.Nonnull;


/**
 * Author Nenad Krdzavac<br>
 * email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 01.08.2022
 */
public class UploadGitFileDialogController extends WebProtegeOKCancelDialogController<String> {

    private UploadGitFileDialogForm gitForm = new UploadGitFileDialogForm();

    public UploadGitFileDialogController(String title, final UploadFileResultHandler resultHandler) {
        super(title);
        setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<String>() {
            @Override
            public void handleHide(String data, final WebProtegeDialogCloser closer) {
                ProgressMonitor.get().showProgressMonitor("Uploading", "Uploading file");
                gitForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
                    public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                        ProgressMonitor.get().hideProgressMonitor();
                        GWT.log("Submittion of file is complete");
                        Log.info("Submittion of file is complete");
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
                gitForm.submit();
            }
        });
    }


    @Nonnull
    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return gitForm.getInitialFocusable();
    }

    @Override
    public String getData() {
        return gitForm.getFileName();
    }

    @Override
    public Widget getWidget() {
        return gitForm;
    }
}
