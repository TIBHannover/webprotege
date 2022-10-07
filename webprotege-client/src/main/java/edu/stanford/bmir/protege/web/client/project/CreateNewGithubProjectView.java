package edu.stanford.bmir.protege.web.client.project;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;

import javax.annotation.Nonnull;

/**
 * Author: Nenad Krdzavac<br>
 * Leibniz University, Hannover, Germany<br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date: 26/04/2022
 */
public interface CreateNewGithubProjectView extends HasInitialFocusable, IsWidget {

    @Nonnull
    String getProjectName();

    /**
     *
     * @return gets remote source Github repository
     */
    @Nonnull
    String getRepoURIField();


    @Nonnull
    String getPersonalAccessTokenField();

    /**
     * Sets the URL that the upload should be posted to.  This is the URL that would be set in the "action" field
     * on an HTML form.
     * @param url The post URL.
     */
    void setGitFileUploadPostUrl(@Nonnull String url, String token);

    /**
     * Determines whether a file (for upload) has been supplied.
     * @return true if a file has been supplied, otherwise false.
     */
//    boolean isFileUploadSpecified();

    boolean isGitFileUploadSpecified();
    /**
     * Clears the information in the view.
     */
    void clear();

    /**
     * Sets a handler for when the form submission is complete.  The form submission is only relevant for file uploads.
     */
    void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler);

    /**
     * Asks the file upload form to submit the form data.
     */
    void submitFormData();

    /**
     * Displays a message saying that the project name is missing
     */
    void showProjectNameMissingMessage();

    void showRemoteGithubRepositoryMissingMessage();

    void showGitAccessTokenMissingMessage();
}
