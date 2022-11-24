package edu.stanford.bmir.protege.web.client.upload;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
    //    SafeHtmlBuilder builder = new SafeHtmlBuilder();
     //   String safeHtml = builder.toSafeHtml().asString();
        repoCloneArea = new HTMLPanel("<div></div>");
        add(uploadSelectorField);
        Log.info("Url for file upload: " + SUBMIT_FILE_URL);

        setPostURL(SUBMIT_FILE_URL);
        Log.info("1");
        fileUpload = new FileUpload();
        Log.info("2");
        fileUpload.setName("file");
        Log.info("3");
        addWidget(FILE_NAME_FIELD_LABEL, fileUpload);
        Log.info("4");
        fileUpload.setWidth("300px");
        Log.info("5");
  //      fileUpload.getElement().<InputElement>cast().click();

        uploadSelectorField.setText("Upload from file");
        Log.info("6");
        uploadSelectorField.setName("yesNoButton");
        Log.info("7");
        uploadSelectorField.setValue(true);
        Log.info("8");
        cloneSelectorField.setText("Clone from repo");
        Log.info("9");
        cloneSelectorField.setName("yesNoButton");
        Log.info("10");
        cloneSelectorField.setValue(false);
        Log.info("11");
        repoVerticalPanel.add(user);
        Log.info("12");
        repoVerticalPanel.add(project);
        Log.info("13");
        repoVerticalPanel.add(path);
        Log.info("14");
        repoVerticalPanel.add(branch);
        Log.info("15");
        repoVerticalPanel.add(repoURI);
        Log.info("16");
        repoVerticalPanel.add(personalAccessToken);
        Log.info("17");
        repoFormPanel.setWidget(repoVerticalPanel);
        add(cloneSelectorField);
        setGitCloneUrl();
        add(repoFormPanel);
        repoURIField.setWidth("300px");
        branchField.setWidth("300px");
        Log.info("21");
        pathField.setWidth("300px");
        Log.info("22");
        repoCloneArea.add(repoURILabel);
        repoCloneArea.add(repoURIField);
        repoCloneArea.add(branchLabel);
        Log.info("23");
        repoCloneArea.add(branchField);
        Log.info("24");
        repoCloneArea.add(pathLabel);
        Log.info("25");
        repoCloneArea.add(pathField);
        Log.info("26");
        add(repoCloneArea);
        addDialogValidator(new FileNameValidator());
        Log.info("27");
        setValue(false);
        setupHandlers();
        Log.info("28");
    }

    public String getFileName() {
        return fileUpload.getFilename().trim();
    }

    public void setValue(Boolean value) {
        this.backingSelectorValue = value;
        if (this.backingSelectorValue) {
            cloneSelectorField.setValue(true);
      //      fileUploadArea.setVisible(false);
            fileUpload.setVisible(false);
            fileUpload.setEnabled(false);
            repoCloneArea.setVisible(true);
            branchField.setVisible(true);
            branchField.setEnabled(true);
            pathField.setVisible(true);
            pathField.setEnabled(true);
            repoURILabel.setText(MESSAGES.repoURI()+" (*)");
        } else {
            uploadSelectorField.setValue(true);
      //      fileUploadArea.setVisible(true);
            fileUpload.setVisible(true);
            fileUpload.setEnabled(true);
            repoCloneArea.setVisible(false);
            branchField.setVisible(false);
            branchField.setEnabled(false);
            pathField.setVisible(false);
            pathField.setEnabled(false);
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
        Log.info("repoURI: "+repoURIField.getValue()+" - "+repoURIField.getText());
        repoURI.setValue(repoURIField.getValue());
        Log.info("personalAccessToken: "+token);
        personalAccessToken.setValue(token);
        Log.info("user: "+userName);
        user.setValue(userName);
        Log.info("project: "+projectName);
        project.setValue(projectName);
        Log.info("path: "+pathField.getValue()+" - "+pathField.getText());
        path.setValue(pathField.getValue());
        Log.info("branch: "+branchField.getValue()+" - "+branchField.getText());
        branch.setValue(branchField.getValue());
    }


    private void setupHandlers() {
        cloneSelectorField.addClickHandler(new UploadAndCloneDialogForm.RadioButtonClickHandler(true));
        uploadSelectorField.addClickHandler(new UploadAndCloneDialogForm.RadioButtonClickHandler(false));
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