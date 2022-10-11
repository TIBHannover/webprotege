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

    private static CreateNewProjectViewImplUiBinder ourUiBinder = GWT.create(CreateNewProjectViewImplUiBinder.class);

    @UiField
    TextBox projectNameField;

    @UiField
    TextArea projectDescriptionField;

    @UiField
    CheckBox repoCreationSelectorField;

    @UiField
    TextBox repoURIField;

    @UiField
    FileUpload fileUpload;

    @UiField
    FormPanel formPanel;

    @UiField
    FormPanel repoFormPanel;

    @UiField
    Hidden repoData;

    @UiField
    HTMLPanel fileUploadArea;

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

        repoCreationSelectorField.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if(getRepoCreationSelector()){
                    fileUploadArea.setVisible(false);
                    fileUpload.setVisible(false);
                    fileUpload.setEnabled(false);
                } else {
                    fileUploadArea.setVisible(true);
                    fileUpload.setVisible(true);
                    fileUpload.setEnabled(true);
                }
            }
        } );
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
    public boolean getRepoCreationSelector(){ return repoCreationSelectorField.getValue(); }

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
        repoData.setName("repoData");
        repoData.setValue(repoURIField.getValue()+"#parse#"+loggedInUserProvider.getCurrentUserToken()+"#parse#"+loggedInUserProvider.getCurrentUserId().getUserName()+"#parse#"+ projectName);
        repoFormPanel.setMethod(METHOD_GET);
        repoFormPanel.setEncoding(ENCODING_URLENCODED);
        repoFormPanel.setAction(checkNotNull(url));
    }

    @Override
    public boolean isFileUploadSpecified() {
        String filename = fileUpload.getFilename();
        return !filename.trim().isEmpty();
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