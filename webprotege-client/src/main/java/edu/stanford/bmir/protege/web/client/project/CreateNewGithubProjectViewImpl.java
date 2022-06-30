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

import com.allen_sauer.gwt.log.client.Log;

/**
 * name Nenad Krdzavac
 * email nenad.krdzavac@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology and University Library
 * 27.06.2022
 */
public class CreateNewGithubProjectViewImpl extends Composite implements CreateNewGithubProjectView {

    interface CreateNewGithubProjectViewImplUiBinder extends UiBinder<HTMLPanel, CreateNewGithubProjectViewImpl> {
    }

    private static CreateNewGithubProjectViewImplUiBinder ourGithubUiBinder = GWT.create(CreateNewGithubProjectViewImplUiBinder.class);


    String projectNameField;
    String remoteGithubOntologyFileURL;

    String githubAccessTokenKey;

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



//  @UiField
//  TextBox githubUserName;

//  @UiField
//  TextBox githubPassword;

    @UiField
    TextBox githubAccessToken;

//  @UiField
//  TextArea projectDescriptionField;

    //  @UiField
    FileUpload gitFileUpload;

    //  @UiField
    FormPanel formPanel;

//  @UiField
//  HTMLPanel fileUploadArea;

//  @UiField(provided = true)
//  DefaultLanguageEditor projectLanguageField;

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
    public String getProjectName() {

        projectNameField = getRemoteGithubRepositoryURL().substring(getRemoteGithubRepositoryURL().lastIndexOf("/")+1);
        return projectNameField;
    }

    @Nonnull
    @Override
    public String getRemoteGithubOntologyFileURL(){

        remoteGithubOntologyFileURL = getRemoteGithubRepositoryURL().toString();

        return remoteGithubOntologyFileURL;
    }


    @Nonnull
    @Override
    public String getGithubAccessTokenKey(){

        githubAccessTokenKey = getGithubAccessToken().toString();

        return githubAccessTokenKey;
    };


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

    @Override
    public void setGitFileUploadPostUrl(@Nonnull String url){

        GWT.log("setGitFileUploadPostUrl: " + url);

        gitFileUpload.setName("gitfile");

        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setEncoding(FormPanel.ENCODING_URLENCODED);
        formPanel.setAction(checkNotNull(url));
    }
//    @Override
//    public boolean isFileUploadSpecified() {
//        String filename = fileUpload.getFilename();
//        return !filename.trim().isEmpty();
//    }

    @Override
    public boolean isGitFileUploadSpecified() {

        String fileName = getProjectName();

        Log.info("file name (getProjectName()): " + getProjectName());

        return !fileName.trim().isEmpty();
    }

    @Override
    public void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler) {
        submitCompleteHandlerRegistraion.removeHandler();
        submitCompleteHandlerRegistraion = formPanel.addSubmitCompleteHandler(handler);
    }

    @Override
    public void submitFormData() {
        formPanel.submit();
    }

    @Override
    public void showProjectNameMissingMessage() {
        messageBox.showAlert("Project name is missing", "Please enter a project name");
    }

    @Override
    public void showGitAccessTokenMissingMessage(){

        messageBox.showAlert("Git access token is missing", "please enter valid access token");
    }

    @Override
    public void showRemoteGithubRepositoryMissingMessage(){
        messageBox.showAlert("Ontology URL in remote Github repository is missing",
                "Please enter ontology URL");
    }
//    @Override
//    public void showRemoteGithubRepositoryMissingMessage(){
//    messageBox.showAlert("Remote Github repository URL is missing", "Please enter Github repository URL");
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