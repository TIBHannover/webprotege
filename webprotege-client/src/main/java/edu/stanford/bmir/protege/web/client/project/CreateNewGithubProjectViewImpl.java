package edu.stanford.bmir.protege.web.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Nenad Krdzavac TIB-Leibniz Information Centre for Science and Technology and University Library 26 April 2022
 * nenad.krdzavac@tib.eu
 */
public class CreateNewGithubProjectViewImpl extends Composite implements CreateNewGithubProjectView {

    interface CreateNewGithubProjectViewImplUiBinder extends UiBinder<HTMLPanel, CreateNewGithubProjectViewImpl> {
    }

    private static CreateNewGithubProjectViewImplUiBinder ourGithubUiBinder = GWT.create(CreateNewGithubProjectViewImplUiBinder.class);

    /**
     * automatically generated
     * @return
     */
//    @Override
//    public Optional<HasRequestFocus> getInitialFocusable() {
//        return Optional.empty();
//    }

    /**
     * author: nenad.krdzavac@tib.eu
     * Project Github URI
     */
    @UiField
    TextBox remoteGithubRepositoryURL;

//    @UiField
//    TextBox githubUserName;

//    @UiField
//    TextBox githubPassword;

    @UiField
    TextBox githubAccessToken;

//    @UiField
//    TextArea projectDescriptionField;

//    @UiField
//    FileUpload fileUpload;

//    @UiField
//    FormPanel formPanel;

//    @UiField
//    HTMLPanel fileUploadArea;

//    @UiField(provided = true)
//    DefaultLanguageEditor projectLanguageField;

    @Nonnull
    private final MessageBox messageBox;

    private HandlerRegistration submitCompleteHandlerRegistraion = () -> {};


    @Inject
    public CreateNewGithubProjectViewImpl(@Nonnull MessageBox messageBox) {
        this.messageBox = checkNotNull(messageBox);
//        this.projectLanguageField = checkNotNull(languageEditor);
//        this.messageBox = messageBox;
        initWidget(ourGithubUiBinder.createAndBindUi(this));
    }

    @Nonnull
    @Override
    public String getRemoteGithubRepositoryURL(){

        return remoteGithubRepositoryURL.getText().trim();
    }

//    @Nonnull
////    @Override
////    public String getGithubUserName(){
////
////        return githubUserName.getText().trim();
////    }

//    @Nonnull
//    @Override
//    public String getGithubPassword(){
//
//        return githubPassword.getText().trim();
//    }

    @Nonnull
    @Override
    public String getGithubAccessToken(){

        return githubAccessToken.getText().trim();
    }

//    @Nonnull
//    @Override
//    public String getRemoteSourceGitRepository() {
//        return null;
//    }

//    @Nonnull
//    @Override
//    public String getProjectDescription() {
//        return projectDescriptionField.getText().trim();
//    }

//    @Nonnull
//    @Override
//    public String getProjectLanguage() {
//        return projectLanguageField.getValue().orElse("").trim();
//    }

//    @Override
//    public void setFileUploadEnabled(boolean enabled) {
//        fileUpload.setEnabled(enabled);
//        fileUploadArea.setVisible(enabled);
//    }

//    @Override
//    public void setFileUploadPostUrl(@Nonnull String url) {
//        fileUpload.setName("file");
//        formPanel.setMethod(METHOD_POST);
//        formPanel.setEncoding(ENCODING_MULTIPART);
//        formPanel.setAction(checkNotNull(url));
//    }

//    @Override
//    public boolean isFileUploadSpecified() {
//        String filename = fileUpload.getFilename();
//        return !filename.trim().isEmpty();
//    }

//    @Override
//    public void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler) {
//        submitCompleteHandlerRegistraion.removeHandler();
//        submitCompleteHandlerRegistraion = formPanel.addSubmitCompleteHandler(handler);
//    }

//    @Override
//    public void submitFormData() {
//        formPanel.submit();
//    }

    @Override
    public void showProjectNameMissingMessage() {
        messageBox.showAlert("Project name missing", "Please enter a project name");
    }

//    @Override
//    public void showRemoteGithubRepositoryMissingMessage(){
//
//        messageBox.showAlert("Remote Github repository URL is missing", "Please enter Github repository URL");
//    }

    @Override
    public void clear() {
        remoteGithubRepositoryURL.setText("");
//        githubUserName.setText("");

    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> remoteGithubRepositoryURL.setFocus(true));
    }

//    @Override
//    protected void onAttach() {
//        super.onAttach();
//        projectNameField.setFocus(true);
//    }
}