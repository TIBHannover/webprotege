package edu.stanford.bmir.protege.web.client.project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.web.bindery.event.shared.EventBus;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;
import edu.stanford.bmir.protege.web.client.projectmanager.ProjectCreatedEvent;
import edu.stanford.bmir.protege.web.client.upload.FileUploadResponse;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserManager;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.csv.DocumentId;
import edu.stanford.bmir.protege.web.shared.permissions.PermissionDeniedException;
import edu.stanford.bmir.protege.web.shared.project.*;
import edu.stanford.bmir.protege.web.shared.upload.FileUploadResponseAttributes;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPLOAD_PROJECT;

/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 11.10.2022
 */

public class CreateNewProjectPresenter {

    private final DispatchErrorMessageDisplay errorDisplay;

    private final ProgressDisplay progressDisplay;

    public interface ProjectCreatedHandler {
        void handleProjectCreated();
    }

    @Nonnull
    private final CreateNewProjectView view;

    @Nonnull
    private final LoggedInUserManager loggedInUserManager;

    @Nonnull
    private final LoggedInUserProvider loggedInUserProvider;

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final EventBus eventBus;

    @Nonnull
    private final MessageBox messageBox;

    private boolean validRepoURI = true;
    @AutoFactory
    @Inject
    public CreateNewProjectPresenter(DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, @Nonnull CreateNewProjectView view,
                                     @Nonnull LoggedInUserManager loggedInUserManager,
                                     @Provided LoggedInUserProvider loggedInUserProvider,
                                     @Nonnull DispatchServiceManager dispatchServiceManager,
                                     @Nonnull EventBus eventBus,
                                     @Nonnull MessageBox messageBox) {
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.view = checkNotNull(view);
        this.loggedInUserManager = checkNotNull(loggedInUserManager);
        this.loggedInUserProvider = checkNotNull(loggedInUserProvider);
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.eventBus = checkNotNull(eventBus);
        this.messageBox = messageBox;
    }

    @Nonnull
    public CreateNewProjectView getView() {
        return view;
    }

    public void start() {

        view.setCloneEnabled(true);

        view.clear();
        if (loggedInUserManager.isAllowedApplicationAction(UPLOAD_PROJECT)) {
            view.setFileUploadEnabled(true);
        }
        else {
            view.setFileUploadEnabled(false);
        }
    }

    private boolean validate() {

        validRepoURI = true;

        if (view.getProjectName().isEmpty()) {
            view.showProjectNameMissingMessage();
            return false;
        }

        if (view.getRepoCreationSelector()){
            if(view.getRepoURI().isEmpty()){
                view.showProjectRepoURIMissingMessage();
                return false;
            }

            validateRepoURI(view.getRepoURI(), loggedInUserProvider.getCurrentUserToken());
        }

        return true;
    }

    public void validateAndCreateProject(ProjectCreatedHandler handler) {
        if (validate()) {
            submitCreateProjectRequest(handler);
        }
    }


    private void submitCreateProjectRequest(ProjectCreatedHandler handler) {

        if (view.getRepoCreationSelector())
            cloneRepoAndCreateProject(handler);
        else if (view.isFileUploadSpecified()) {
            uploadSourcesAndCreateProject(handler);
        }
        else {
            createEmptyProject(handler);
        }
    }

    private void createEmptyProject(ProjectCreatedHandler projectCreatedHandler) {
        NewProjectSettings newProjectSettings = NewProjectSettings.get(
                loggedInUserManager.getLoggedInUserId(),
                view.getProjectName(),
                view.getProjectLanguage(),
                view.getProjectDescription(),
                view.getRepoURI());
        submitCreateNewProjectRequest(newProjectSettings, projectCreatedHandler);
    }

    private void cloneRepoAndCreateProject(@Nonnull ProjectCreatedHandler projectCreatedHandler){
        checkNotNull(projectCreatedHandler);
        String getURL = GWT.getModuleBaseURL() + "submitgitfile";
        Log.info("GWT.getModuleBaseURL(): " + GWT.getModuleBaseURL().toString());
        Log.info("GWT.getHostPageBaseURL(): " + GWT.getHostPageBaseURL());
        Log.info("getUrl: " + getURL);
        Log.info("");
        view.setGitClonePostUrl(getURL, loggedInUserProvider, view.getProjectName());

        ProgressMonitor.get().showProgressMonitor("Cloning repo", "Uploading file");

        view.setGitSubmitCompleteHandler(event -> {
            ProgressMonitor.get().hideProgressMonitor();
            handleSourcesCloneComplete(event, projectCreatedHandler);
        });

        view.submitGitFormData();
    }


    private void uploadSourcesAndCreateProject(@Nonnull ProjectCreatedHandler projectCreatedHandler) {
            checkNotNull(projectCreatedHandler);

            String postUrl = GWT.getModuleBaseURL() + "submitfile";

            Log.info("GWT.getModuleBaseURL(): " + GWT.getModuleBaseURL().toString());
            Log.info("postUrl: " + postUrl);
            Log.info("");

            view.setFileUploadPostUrl(postUrl);

            ProgressMonitor.get().showProgressMonitor("Uploading sources", "Uploading file");

            view.setSubmitCompleteHandler(event -> {
                ProgressMonitor.get().hideProgressMonitor();
                handleSourcesUploadComplete(event, projectCreatedHandler);
            });

            view.submitFormData();
    }

    private void handleSourcesUploadComplete(FormPanel.SubmitCompleteEvent event,
                                             ProjectCreatedHandler projectCreatedHandler) {
        FileUploadResponse response = new FileUploadResponse(event.getResults());

        if (response.wasUploadAccepted()) {
            DocumentId documentId = response.getDocumentId();
            NewProjectSettings newProjectSettings = NewProjectSettings.get(
                    loggedInUserManager.getLoggedInUserId(),
                    view.getProjectName(),
                    view.getProjectLanguage(),
                    view.getProjectDescription(),
                    documentId,
                    view.getRepoURI()
            );
            submitCreateNewProjectRequest(newProjectSettings, projectCreatedHandler);
        }
        else {
            messageBox.showAlert("Upload Failed", response.getUploadRejectedMessage());
        }
    }


    private void handleSourcesCloneComplete(FormPanel.SubmitCompleteEvent event,
                                             ProjectCreatedHandler projectCreatedHandler) {
        JSONValue value = JSONParser.parseLenient(event.getResults());
        JSONObject object = value.isObject();
        DocumentId documentId;
        if (object == null)
            documentId = new DocumentId("");
        else {
            JSONValue value1 = object.get(FileUploadResponseAttributes.UPLOAD_FILE_ID.name());
            if (value1.isString() == null)
                documentId = new DocumentId("");
            else
                documentId = new DocumentId(value1.isString().stringValue().trim());
        }

        if (object != null) {
            NewProjectSettings newProjectSettings = NewProjectSettings.get(
                    loggedInUserManager.getLoggedInUserId(),
                    view.getProjectName(),
                    view.getProjectLanguage(),
                    view.getProjectDescription(),
                    documentId,
                    view.getRepoURI()
            );
            submitCreateNewProjectRequest(newProjectSettings, projectCreatedHandler);
        }
        else {
            messageBox.showAlert("Clone Failed", "No ontology could be found");
        }
    }


    private void submitCreateNewProjectRequest(@Nonnull NewProjectSettings newProjectSettings,
                                               @Nonnull ProjectCreatedHandler projectCreatedHandler) {
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
                        Log.info("result.getProjectDetails().getOwner().toString(): " + result.getProjectDetails().getOwner().toString());
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
        return new NewProjectInfo(view.getProjectName(), view.getProjectDescription());
    }

    private void validateRepoURI(String repoURI, String token){
        String trackerType = null;
        if (repoURI.toLowerCase().startsWith("https://github.com") || repoURI.toLowerCase().startsWith("http://github.com"))
            trackerType = "github.com";
        else if (repoURI.toLowerCase().startsWith("https://gitlab.com") || repoURI.toLowerCase().startsWith("http://gitlab.com"))
            trackerType = "gitlab.com";
        else {
            if(repoURI.toLowerCase().split("https://").length > 1)
                trackerType = repoURI.toLowerCase().split("https://")[1].split("/")[0];
            else if(repoURI.toLowerCase().split("http://").length > 1)
                trackerType = repoURI.toLowerCase().split("http://")[1].split("/")[0];
        }
        if(trackerType.equals("github.com"))
            callGithub(convertRepoURI2CallURL(repoURI, trackerType),token, trackerType);
        else if (trackerType != null)
            callGitlab(convertRepoURI2CallURL(repoURI, trackerType),token, trackerType);
        else
            repoExists(false);
    }

    public String convertRepoURI2CallURL(String repoURI, String trackerType){
        if (trackerType.equals("github.com")){
            String[] parsedRepoUrl = repoURI.split("/");
            StringBuilder sb = new StringBuilder();
            String institution = "";
            String user = "";
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if(i == 2)
                    sb.append("api.");
                if (i == 3) {
                    sb.append("repos").append("/");
                    institution = parsedRepoUrl[i];
                }
                if (i ==4)
                    user = parsedRepoUrl[i];
                sb.append(parsedRepoUrl[i]).append("/");
            }
            sb.append("branches");
            return sb.toString();
        } else if (trackerType.contains(".")) {
            String[] parsedRepoUrl = repoURI.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i ==2) {
                    Log.info("trackerType: "+trackerType);
                    Log.info("parsedRepoUrl[i]: "+parsedRepoUrl[i]);
                    if(!trackerType.equals(parsedRepoUrl[i]))
                        return "";
                    sb.append("https://"+trackerType+"/api/v4/projects/");
                }

                if (i > 2) {
                    sb.append(parsedRepoUrl[i]);
                }
                if (i > 2 && i <parsedRepoUrl.length - 1)
                    sb.append("%2F");

            }

            return sb.toString();
        } else return "";
    }

    public void callGithub(String callUrl, String token, String trackerType) {
        Log.info("callUrl: "+callUrl);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);
        requestBuilder.setHeader("Accept", "application/vnd.github+json");
        if(token!=null)
            if(!token.isEmpty())
                requestBuilder.setHeader("Authorization", "Bearer "+token);
        // requestBuilder.setIncludeCredentials(true);

        try {
            Request response = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                        repoExists(true);
                    } else {
                        repoExists(false);
                    }
                }

                public void onError(Request request, Throwable exception) {
                    repoExists(false);
                }

            });

        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    public void callGitlab(String callUrl, String token, String trackerType){
        Log.info("callUrl: "+callUrl);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);
        if(token!=null)
            if(!token.isEmpty())
                requestBuilder.setHeader("Authorization", "Bearer "+token);
        // requestBuilder.setIncludeCredentials(true);
        try {
            Request response = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                        repoExists(true);
                    } else {
                        repoExists(false);
                    }
                }
                public void onError(Request request, Throwable exception) {
                    repoExists(false);
                }
            });
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    public void repoExists(boolean success) {
        if(!success){
            validRepoURI = false;
            view.showRepoUnavailableMessage();
        }
    }
}
