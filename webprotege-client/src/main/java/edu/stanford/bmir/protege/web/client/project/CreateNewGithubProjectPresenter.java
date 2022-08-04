package edu.stanford.bmir.protege.web.client.project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.web.bindery.event.shared.EventBus;
import edu.stanford.bmir.protege.web.client.app.Presenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.projectmanager.ProjectCreatedEvent;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;
import edu.stanford.bmir.protege.web.client.upload.FileUploadResponse;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserManager;
import edu.stanford.bmir.protege.web.shared.csv.DocumentId;
import edu.stanford.bmir.protege.web.shared.permissions.PermissionDeniedException;
import edu.stanford.bmir.protege.web.shared.project.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPLOAD_PROJECT;



/**
 * Author: Nenad Krdzavac<br>
 * email: nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date: 11/05/2022
 */
public class CreateNewGithubProjectPresenter  {

    private final DispatchErrorMessageDisplay errorDisplay;

    private final ProgressDisplay progressDisplay;


    public interface GithubProjectCreatedHandler {
        void handleProjectCreated();
    }

    @Nonnull
    private final CreateNewGithubProjectView view;

    @Nonnull
    private final LoggedInUserManager loggedInUserManager;

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final EventBus eventBus;

    @Nonnull
    private final MessageBox messageBox;

    @Inject
    public CreateNewGithubProjectPresenter(DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, @Nonnull CreateNewGithubProjectView view,
                                           @Nonnull LoggedInUserManager loggedInUserManager,
                                           @Nonnull DispatchServiceManager dispatchServiceManager,
                                           @Nonnull EventBus eventBus,
                                           @Nonnull MessageBox messageBox) {
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.view = checkNotNull(view);
        this.loggedInUserManager = checkNotNull(loggedInUserManager);
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.eventBus = checkNotNull(eventBus);
        this.messageBox = messageBox;
    }

    @Nonnull
    public CreateNewGithubProjectView getView() {
        return view;
    }

    public void start() {
        view.clear();

        Log.info("public void start()");

        view.setFileUploadEnabled(true);

//        if (loggedInUserManager.isAllowedApplicationAction(UPLOAD_PROJECT)) {
//
//            view.setFileUploadEnabled(true);
//        }
//        else {
//            view.setFileUploadEnabled(false);
//        }
    }

    private boolean validate() {

        if (view.getRemoteGithubRepositoryURL().isEmpty()) {

            view.showRemoteGithubRepositoryMissingMessage();
            return false;
        }

        if(view.getGithubAccessToken().isEmpty()){

            view.showGitAccessTokenMissingMessage();

            return false;
        }

        return true;
    }

    public void validateAndCreateProject(GithubProjectCreatedHandler handler) {
        if (validate()) {
            submitCreateProjectRequest(handler);
        }
    }


    private void submitCreateProjectRequest(GithubProjectCreatedHandler handler) {

//        uploadSourcesAndCreateGitProject(handler);
        Log.info("submitCreateProjectRequest: ");
        Log.info("   - view.getRemoteGithubRepositoryURL(): " + view.getRemoteGithubRepositoryURL());
        Log.info("   - view.getRemoteGithubRepositoryURL(): " + view.getGithubAccessToken());


//            if (view.isGitFileUploadSpecified()) {

//            logger.log(Level.SEVERE, "submitCreateGitProjectRequest (if) : submitCreateGitProjectRequest: Creates new github empty project");

//                Log.info("submitCreateGitProjectRequest (if) : submitCreateGitProjectRequest: call uploadSourcesAndCreateGitProject(handler)");
//                createEmptyGitProject(handler);
                uploadSourcesAndCreateGitProject(handler);

//            }
//            else {
//                Log.info("submitCreateGitProjectRequest (else) : submitCreateGitProjectRequest: Creates new github empty project");
//                createEmptyGitProject(handler);
//
//            }
        }

//        if (view.isFileUploadSpecified()) {
//            uploadSourcesAndCreateProject(handler);
//        }
//        else {
//            createEmptyProject(handler);
//        }


    private void createEmptyGitProject(GithubProjectCreatedHandler projectCreatedHandler) {

        Log.info("createEmptyGitProject: " );
        Log.info("  - view.getProjectName(): " + view.getProjectName());

        NewProjectSettings newProjectSettings = NewProjectSettings.get(
                loggedInUserManager.getLoggedInUserId(),
                view.getProjectName(),
                "",
                "");
        submitCreateNewGitProjectRequest(newProjectSettings, projectCreatedHandler);
    }

    private void uploadSourcesAndCreateGitProject(@Nonnull GithubProjectCreatedHandler projectCreatedHandler) {
        checkNotNull(projectCreatedHandler);

        String postUrl = GWT.getModuleBaseURL() + "submitgitfile";

        Log.info("postUrl: " + postUrl);

        view.setGitFileUploadPostUrl(postUrl);
        ProgressMonitor.get().showProgressMonitor("Uploading sources", "Uploading file");
        view.setSubmitCompleteHandler(event -> {
            ProgressMonitor.get().hideProgressMonitor();
            handleSourcesUploadComplete(event, projectCreatedHandler);
        });
        view.submitFormData();
    }


    private void handleSourcesUploadComplete(FormPanel.SubmitCompleteEvent event,
                                             GithubProjectCreatedHandler githubprojectCreatedHandler) {
        FileUploadResponse response = new FileUploadResponse(event.getResults());
        if (response.wasUploadAccepted()) {
            DocumentId documentId = response.getDocumentId();
            NewProjectSettings newGitProjectSettings = NewProjectSettings.get(
                    loggedInUserManager.getLoggedInUserId(),
                    view.getProjectName(),
                    "",
                    "",
                    documentId
            );

            Log.info("handleGitSourcesUploadComplete: view.getRemoteGithubRepositoryURL(): " + view.getRemoteGithubRepositoryURL());
            Log.info("handleGitSourcesUploadComplete: view.getGithubAccessToken(): " + view.getGithubAccessToken());

            submitCreateNewGitProjectRequest(newGitProjectSettings, githubprojectCreatedHandler);
        }
        else {
            messageBox.showAlert("Upload Failed", response.getUploadRejectedMessage());
        }
    }

    private void submitCreateNewGitProjectRequest(@Nonnull NewProjectSettings newProjectSettings,
                                               @Nonnull GithubProjectCreatedHandler projectCreatedHandler) {
        dispatchServiceManager.execute(new CreateNewProjectAction(newProjectSettings),
                new DispatchServiceCallbackWithProgressDisplay<CreateNewProjectResult>(errorDisplay,
                                                                                       progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Creating project";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait.";
                    }

                    @Override
                    public void handleSuccess(CreateNewProjectResult result) {
                        projectCreatedHandler.handleProjectCreated();
                        eventBus.fireEvent(new ProjectCreatedEvent(result.getProjectDetails()));
                    }

                    @Override
                    public void handleExecutionException(Throwable cause) {
                        if (cause instanceof PermissionDeniedException) {
                            messageBox.showMessage("You do not have permission to create new projects");
                        }
                        else if (cause instanceof ProjectAlreadyRegisteredException) {
                            ProjectAlreadyRegisteredException ex = (ProjectAlreadyRegisteredException) cause;
                            String projectName = ex.getProjectId().getId();
                            messageBox.showMessage("The project name " + projectName + " is already registered.  Please try a different name.");
                        }
                        else if (cause instanceof ProjectDocumentExistsException) {
                            ProjectDocumentExistsException ex = (ProjectDocumentExistsException) cause;
                            String projectName = ex.getProjectId().getId();
                            messageBox.showMessage("There is already a non-empty project on the server with the id " + projectName + ".  This project has NOT been overwritten.  Please contact the administrator to resolve this issue.");
                        }
                        else {
                            messageBox.showMessage(cause.getMessage());
                        }
                    }
                });
    }

    public NewProjectInfo getNewProjectInfo() {
        return new NewProjectInfo(view.getProjectName(), "");
    }
}
