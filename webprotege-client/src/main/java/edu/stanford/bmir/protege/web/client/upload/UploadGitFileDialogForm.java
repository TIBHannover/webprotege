package edu.stanford.bmir.protege.web.client.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import edu.stanford.bmir.protege.web.client.library.dlg.ValidationState;
import edu.stanford.bmir.protege.web.client.library.dlg.WebProtegeDialogForm;
import edu.stanford.bmir.protege.web.client.library.dlg.WebProtegeDialogValidator;

/**
 * Author Nenad Krdzavac<br>
 * email nenad.krdzavac@tib.eu<br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date: 01.08.2022
 */
public class UploadGitFileDialogForm extends WebProtegeDialogForm {

    private static final String SUBMIT_FILE_URL = GWT.getModuleBaseURL() + "submitgitfile";

    private final FileUpload gitFileUpload;

    public static final String FILE_NAME_FIELD_LABEL = "File";

    public UploadGitFileDialogForm() {

        Log.info("SUBMIT_FILE_URL" + SUBMIT_FILE_URL);

        setPostURL(SUBMIT_FILE_URL);
        gitFileUpload = new FileUpload();
        gitFileUpload.setName("gitfile");
        addWidget(FILE_NAME_FIELD_LABEL, gitFileUpload);
        gitFileUpload.setWidth("300px");

        gitFileUpload.getElement().<InputElement>cast().click();


        addDialogValidator(new FileNameValidator());
    }

    public String getFileName() {
        return gitFileUpload.getFilename().trim();
    }


    private class FileNameValidator implements WebProtegeDialogValidator {

        public ValidationState getValidationState() {
            return getFileName().isEmpty() ? ValidationState.INVALID : ValidationState.VALID;
        }

        public String getValidationMessage() {
            return "A file name must be specified.  Please specify a file name";
        }
    }
}