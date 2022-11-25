package edu.stanford.bmir.protege.web.client.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.ValidationState;
import edu.stanford.bmir.protege.web.client.library.dlg.WebProtegeDialogForm;
import edu.stanford.bmir.protege.web.client.library.dlg.WebProtegeDialogValidator;
import edu.stanford.bmir.protege.web.client.project.CreateNewProjectViewImpl;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import org.apache.tapestry.form.Radio;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.gwt.user.client.ui.FormPanel.*;
import static com.google.gwt.user.client.ui.FormPanel.ENCODING_URLENCODED;

/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 21.11.2022
 */
public class UploadAndCloneDialogForm extends WebProtegeDialogForm {

    public final class RadioButtonClickHandler implements ClickHandler {
        private Boolean value;

        public RadioButtonClickHandler(Boolean value) {
            this.value = value;
        }

        @Override
        public void onClick(ClickEvent event) {
            backingSelectorValue = value;
            setValue(value);
        }
    }

    Boolean backingSelectorValue;

    private static final String SUBMIT_FILE_URL = GWT.getModuleBaseURL() + "submitfile";

    RadioButton cloneSelectorField;

    Label repoURILabel;

    TextBox repoURIField;

    Label pathLabel;

    TextBox pathField;

    Label branchLabel;

    TextBox branchField;

    FormPanel repoFormPanel;

    HTMLPanel repoCloneArea;

    VerticalPanel repoVerticalPanel;

    Hidden repoURI;

    Hidden personalAccessToken;

    Hidden user;

    Hidden project;

    Hidden path;

    Hidden branch;

    HTMLPanel fileUploadArea;
    private final FileUpload fileUpload;

    RadioButton uploadSelectorField;

    Label errorLabel;

    private static final Messages MESSAGES = GWT.create(Messages.class);

    public static final String FILE_NAME_FIELD_LABEL = "File";

    public UploadAndCloneDialogForm() {

        uploadSelectorField = new RadioButton("yesNoButton");
        cloneSelectorField = new RadioButton("yesNoButton");
        repoVerticalPanel = new VerticalPanel();
        user = new Hidden("user");
        project = new Hidden("project");
        path= new Hidden("path");
        branch = new Hidden("branch");
        repoURI= new Hidden("repoURI");
        personalAccessToken= new Hidden("personalAccessToken");
        repoFormPanel = new FormPanel();
        repoURILabel = new Label(MESSAGES.repoURI());
        repoURIField = new TextBox();
        branchLabel = new Label("Branch in the repo: ");
        branchField = new TextBox();
        pathLabel = new Label("Relative Path: ");
        pathField = new TextBox();
        repoCloneArea = new HTMLPanel("<div></div>");
        errorLabel= new Label("");
        errorLabel.getElement().getStyle().setColor("red");
        addWidget("",uploadSelectorField);
        Log.info("Url for file upload: " + SUBMIT_FILE_URL);

        setPostURL(SUBMIT_FILE_URL);
        fileUpload = new FileUpload();
        fileUpload.setName("file");
        addWidget("", fileUpload);
        fileUpload.setWidth("300px");
  //      fileUpload.getElement().<InputElement>cast().click();

        uploadSelectorField.setText("Upload from file");
        uploadSelectorField.setName("yesNoButton");
        uploadSelectorField.setValue(true);
        cloneSelectorField.setText("Clone from repo");
        cloneSelectorField.setName("yesNoButton");
        cloneSelectorField.setValue(false);
        repoVerticalPanel.add(user);
        repoVerticalPanel.add(project);
        repoVerticalPanel.add(path);
        repoVerticalPanel.add(branch);
        repoVerticalPanel.add(repoURI);
        repoVerticalPanel.add(personalAccessToken);
        repoFormPanel.setWidget(repoVerticalPanel);
        addWidget("", cloneSelectorField);
        setGitCloneUrl();
        add(repoFormPanel);
        repoURIField.setWidth("300px");
        repoURIField.setTitle(MESSAGES.projectSettings_repouri_helpText().asString());
        branchField.setWidth("300px");
        branchField.setTitle(MESSAGES.projectSettings_branch_helpText().asString());
        pathField.setWidth("300px");
        pathField.setTitle(MESSAGES.projectSettings_repopath_helpText().asString());
        repoCloneArea.add(repoURILabel);
        repoCloneArea.add(repoURIField);
        repoCloneArea.add(branchLabel);
        repoCloneArea.add(branchField);
        repoCloneArea.add(pathLabel);
        repoCloneArea.add(pathField);
        add(repoCloneArea);
        add(errorLabel);
        errorLabel.setVisible(false);
        addDialogValidator(new FileNameValidator());
        addDialogValidator(new URLValidator());
        setValue(false);
        setupHandlers();
    }

    public String getFileName() {
        return fileUpload.getFilename().trim();
    }

    public String getRepoURI() {
        return repoURIField.getText();
    }

    public void setValue(Boolean value) {
        this.backingSelectorValue = value;
        if (this.backingSelectorValue) {
            errorLabel.setVisible(false);
            cloneSelectorField.setValue(true);
      //      fileUploadArea.setVisible(false);
      //    Index is 1 because the widget of fileuploader was added in the 2nd order.
            this.getWidget(1).setVisible(false);
            fileUpload.setVisible(false);
            fileUpload.setEnabled(false);
            repoCloneArea.setVisible(true);
            branchField.setEnabled(true);
            pathField.setEnabled(true);
            repoURIField.setEnabled(true);
            repoURILabel.setText(MESSAGES.repoURI()+" (*)");
        } else {
            errorLabel.setVisible(false);
            uploadSelectorField.setValue(true);
      //      fileUploadArea.setVisible(true);
            this.getWidget(1).setVisible(true);
            fileUpload.setVisible(true);
            fileUpload.setEnabled(true);
            repoCloneArea.setVisible(false);
            branchField.setText("");
            branchField.setEnabled(false);
            pathField.setText("");
            pathField.setEnabled(false);
            repoURIField.setText("");
            repoURIField.setEnabled(false);
            repoURILabel.setText(MESSAGES.repoURI());
        }
    }

    public boolean getRepoCreationSelector(){ return backingSelectorValue; }

    public void setGitCloneUrl(){
        String url = GWT.getModuleBaseURL() + "submitgitfile";
        Log.info("Url for repo clone: " + url);
        repoFormPanel.setMethod(METHOD_GET);
        repoFormPanel.setEncoding(ENCODING_URLENCODED);
        repoFormPanel.setAction(checkNotNull(url));
    }

    public void storeServletValuesInHidden(String token, String userName, String projectName){
        repoURI.setValue(repoURIField.getValue());
        personalAccessToken.setValue(token);
        user.setValue(userName);
        project.setValue(projectName);
        path.setValue(pathField.getValue());
        branch.setValue(branchField.getValue());
        Log.info("repoURI: "+repoURIField.getValue()
                + " - branch: "+branchField.getValue()
                + " - path: "+pathField.getValue()
                + " - user: "+userName
                + " - personalAccessToken: "+token
                + " - project: "+projectName

        );
    }

    private void setupHandlers() {
        cloneSelectorField.addClickHandler(new UploadAndCloneDialogForm.RadioButtonClickHandler(true));
        uploadSelectorField.addClickHandler(new UploadAndCloneDialogForm.RadioButtonClickHandler(false));
    }


    private class FileNameValidator implements WebProtegeDialogValidator {

        public ValidationState getValidationState() {
            return getFileName() == null || getFileName().isEmpty() ? ValidationState.INVALID : ValidationState.VALID;
        }

        public String getValidationMessage() {
            return "A file name must be specified.  Please specify a file name";
        }
    }

    private class URLValidator implements WebProtegeDialogValidator {

        private RegExp urlValidator;
        private RegExp urlPlusTldValidator;

        public ValidationState getValidationState() {
            return isValidUrl(getRepoURI(),false) ? ValidationState.VALID : ValidationState.INVALID;
        }

        public String getValidationMessage() {
            return "A valid url should be entered.  Please make sure that your url is valid";
        }

        public boolean isValidUrl(String url, boolean topLevelDomainRequired) {
            if (urlValidator == null || urlPlusTldValidator == null) {
                urlValidator = RegExp.compile("^((ftp|http|https)://[\\w@.\\-\\_]+(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
                urlPlusTldValidator = RegExp.compile("^((ftp|http|https)://[\\w@.\\-\\_]+\\.[a-zA-Z]{2,}(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
            }
            return (topLevelDomainRequired ? urlPlusTldValidator : urlValidator).exec(url) != null;
        }

    }
}