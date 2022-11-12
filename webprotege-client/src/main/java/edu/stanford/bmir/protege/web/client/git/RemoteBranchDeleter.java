package edu.stanford.bmir.protege.web.client.git;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.git.ProjectCommitConstants.*;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public class RemoteBranchDeleter {

    private final AvailableProject project;

    private final RevisionNumber revisionNumber;

    private final DeleteRemoteBranchData deleteRemoteBranchData;

    /**
     * Constructs a ProjectRevisionCommit for the specified project, revision and project format.
     *
     * @param project The available project id.  Not {@code null}.
     * @param revisionNumber The revision to download.  Not {@code null}.
     * @param deleteRemoteBranchData The format that the project should be downloaded in.  Not {@code null}.
     * @throws  NullPointerException if any parameters are {@code null}.
     */
    public RemoteBranchDeleter(AvailableProject project, RevisionNumber revisionNumber, DeleteRemoteBranchData deleteRemoteBranchData) {
        this.project = checkNotNull(project);
        this.revisionNumber = checkNotNull(revisionNumber);
        this.deleteRemoteBranchData = checkNotNull(deleteRemoteBranchData);
    }

    /**
     * Causes a new browser window to be opened which will commit the specified project revision in the specified
     * format.
     */
    public void switcher(String token) {
        String encodedProjectName = URL.encode(project.getProjectId().getId());
        String encodedRepoURI = URL.encode(project.getRepoURI());
        String encodedToken = URL.encode(token);
        String baseURL = GWT.getHostPageBaseURL();
        String deleteBranchURL = baseURL + "delete?"
                + PROJECT + "=" + encodedProjectName  +
                "&" + REVISION + "=" + revisionNumber.getValue() +
                "&" + REPO_URI + "=" + encodedRepoURI +
                "&" + PERSONAL_ACCESS_TOKEN + "=" + encodedToken +
                "&" + BRANCH + "=" + deleteRemoteBranchData.getBranch();
        Window.open(deleteBranchURL, "Delete branch", "");
    }

}
