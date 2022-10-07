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
    /**
     * author: nenad.krdzavac@tib.eu
     * Project Github URI
     */
    @UiField
    TextBox repoURIField;

/*    @UiField
    TextBox personalAccessTokenField;*/

    @UiField
    FormPanel formPanel;

    @Nonnull
    private final MessageBox messageBox;

    private HandlerRegistration submitCompleteHandlerRegistraion = () -> {};

    @Inject
    public CreateNewGithubProjectViewImpl(@Nonnull MessageBox messageBox) {
        this.messageBox = checkNotNull(messageBox);
        initWidget(ourGithubUiBinder.createAndBindUi(this));
    }

    @Nonnull
    @Override
    public String getRepoURIField(){

        return repoURIField.getText().trim();
    }

    @Nonnull
    @Override
    public String getProjectName() {

        projectNameField = getRepoURIField().substring(getRepoURIField().lastIndexOf("/") + 1);
        return projectNameField;
    }


    @Nonnull
    @Override
    public String getPersonalAccessTokenField(){

        return ""/*personalAccessTokenField.getText().trim()*/;
    }

    @Override
    public void setGitFileUploadPostUrl(@Nonnull String url, String token){

        Log.info("setGitFileUploadPostUrl: " + url);
        repoURIField.setName("repoURI");
        repoURIField.setValue(repoURIField.getValue()+"#token#"+token);
       // personalAccessTokenField.setName("personalAccessToken");
        formPanel.setMethod(FormPanel.METHOD_GET);
        formPanel.setEncoding(FormPanel.ENCODING_URLENCODED);
        formPanel.setAction(checkNotNull(url));
        formPanel.setVisible(false);

    }

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

    @Override
    public void clear() {
        repoURIField.setText("");
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> repoURIField.setFocus(true));
    }
}