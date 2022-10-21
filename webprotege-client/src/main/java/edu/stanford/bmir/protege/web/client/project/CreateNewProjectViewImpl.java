package edu.stanford.bmir.protege.web.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.primitive.DefaultLanguageEditor;

import com.allen_sauer.gwt.log.client.Log;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.gwt.user.client.ui.FormPanel.*;

/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 11.10.2022
 */
public class CreateNewProjectViewImpl extends Composite implements CreateNewProjectView {

    interface CreateNewProjectViewImplUiBinder extends UiBinder<HTMLPanel, CreateNewProjectViewImpl> {
    }

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

    private static CreateNewProjectViewImplUiBinder ourUiBinder = GWT.create(CreateNewProjectViewImplUiBinder.class);

    @UiField
    TextBox projectNameField;

    @UiField
    TextArea projectDescriptionField;

    @UiField
    RadioButton cloneSelectorField;

    @UiField
    RadioButton uploadSelectorField;

    Boolean backingSelectorValue;

    @UiField
    TextBox repoURIField;

    @UiField
    TextBox pathField;

    @UiField
    TextBox branchField;

    @UiField
    FileUpload fileUpload;

    @UiField
    FormPanel formPanel;

    @UiField
    FormPanel repoFormPanel;

    @UiField
    VerticalPanel repoVerticalPanel;

    @UiField
    Hidden repoURI;

    @UiField
    Hidden personalAccessToken;

    @UiField
    Hidden user;

    @UiField
    Hidden project;

    @UiField
    Hidden path;

    @UiField
    Hidden branch;

    @UiField
    HTMLPanel fileUploadArea;

    @UiField
    HTMLPanel repoCloneArea;

    @UiField(provided = true)
    DefaultLanguageEditor projectLanguageField;

    @Nonnull
    private final MessageBox messageBox;

    private HandlerRegistration submitCompleteHandlerRegistration = () -> {};


    @Inject
    public CreateNewProjectViewImpl(@Nonnull DefaultLanguageEditor languageEditor,
                                    @Nonnull MessageBox messageBox) {
        this.projectLanguageField = checkNotNull(languageEditor);
        this.messageBox = messageBox;
        initWidget(ourUiBinder.createAndBindUi(this));
        branchField.setTitle("No blanks. Example: master-2");
        pathField.setTitle("Example: src/dev/tbox/oais-ip-tbox.owl");
        repoURIField.setTitle("Example: https://github.com/fengel/OAIS-IP-Ontology");
        setValue(false);
        setupHandlers();
    }

    @Nonnull
    @Override
    public String getProjectName() {
        return projectNameField.getText().trim();
    }

    @Nonnull
    @Override
    public String getProjectDescription() {
        return projectDescriptionField.getText().trim();
    }

    @Nonnull
    @Override
    public String getProjectLanguage() {
        return projectLanguageField.getValue().orElse("").trim();
    }

    @Nullable
    @Override
    public boolean getRepoCreationSelector(){ return backingSelectorValue; }

    @Nullable
    @Override
    public String getRepoURI() { return repoURIField.getText().trim();}


    @Override
    public void setFileUploadEnabled(boolean enabled) {
        fileUpload.setEnabled(enabled);
        fileUploadArea.setVisible(enabled);
    }
    @Override
    public void setCloneEnabled(boolean enabled){
        repoURIField.setEnabled(true);
    }

    @Override
    public void setFileUploadPostUrl(@Nonnull String url) {

        Log.info("url in method setFileUploadPostUrl of CreateNewProjectViewImpl class : " + url);
        projectNameField.setName("name");
        projectDescriptionField.setName("description");
        fileUpload.setName("file");
        formPanel.setMethod(METHOD_POST);
        formPanel.setEncoding(ENCODING_MULTIPART);
        formPanel.setAction(checkNotNull(url));
    }
    @Override
    public void setGitClonePostUrl(@Nonnull String url, LoggedInUserProvider loggedInUserProvider, String projectName){
        Log.info("url in method setGitClonePostUrl of CreateNewProjectViewImpl class : " + url);

        repoURI.setName("repoURI");
        repoURI.setValue(repoURIField.getValue());
        personalAccessToken.setName("personalAccessToken");
        personalAccessToken.setValue(loggedInUserProvider.getCurrentUserToken());
        user.setName("user");
        user.setValue(loggedInUserProvider.getCurrentUserId().getUserName());
        project.setName("project");
        project.setValue(projectName);
        path.setName("path");
        path.setValue(pathField.getValue());
        branch.setName("branch");
        branch.setValue(branchField.getValue());

        repoFormPanel.setMethod(METHOD_GET);
        repoFormPanel.setEncoding(ENCODING_URLENCODED);
        repoFormPanel.setAction(checkNotNull(url));
    }

    @Override
    public boolean isFileUploadSpecified() {
        String filename = fileUpload.getFilename();
        return !filename.trim().isEmpty();
    }

    private void setupHandlers() {
        cloneSelectorField.addClickHandler(new RadioButtonClickHandler(true));
        uploadSelectorField.addClickHandler(new RadioButtonClickHandler(false));
    }

    public void setValue(Boolean value) {
        this.backingSelectorValue = value;
        if (this.backingSelectorValue) {
            cloneSelectorField.setValue(true);
            fileUploadArea.setVisible(false);
            fileUpload.setVisible(false);
            fileUpload.setEnabled(false);
            repoCloneArea.setVisible(true);
            branchField.setVisible(true);
            branchField.setEnabled(true);
            pathField.setVisible(true);
            pathField.setEnabled(true);
        } else {
            uploadSelectorField.setValue(true);
            fileUploadArea.setVisible(true);
            fileUpload.setVisible(true);
            fileUpload.setEnabled(true);
            repoCloneArea.setVisible(false);
            branchField.setVisible(false);
            branchField.setEnabled(false);
            pathField.setVisible(false);
            pathField.setEnabled(false);
        }
    }

    @Override
    public void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler) {
        submitCompleteHandlerRegistration.removeHandler();
        submitCompleteHandlerRegistration = formPanel.addSubmitCompleteHandler(handler);
    }
    @Override
    public void setGitSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler) {
        submitCompleteHandlerRegistration.removeHandler();
        submitCompleteHandlerRegistration = repoFormPanel.addSubmitCompleteHandler(handler);
    }

    @Override
    public void submitFormData() {
        formPanel.submit();
    }
    @Override
    public void submitGitFormData(){
         repoFormPanel.submit();
    }

    @Override
    public void showProjectNameMissingMessage() {
        messageBox.showAlert("Project name missing", "Please enter a project name");
    }

    @Override
    public void showProjectRepoURIMissingMessage() {
        messageBox.showAlert("Project repo URI missing", "Please enter a project repo URI");
    }


    @Override
    public void clear() {
        projectNameField.setText("");
        projectDescriptionField.setText("");
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> projectNameField.setFocus(true));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        projectNameField.setFocus(true);
    }
}