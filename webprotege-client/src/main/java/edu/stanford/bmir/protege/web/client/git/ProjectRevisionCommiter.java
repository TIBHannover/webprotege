package edu.stanford.bmir.protege.web.client.git;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import edu.stanford.bmir.protege.web.shared.project.AvailableProject;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.git.ProjectCommitConstants.*;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 08.08.2022
 *
 * <p>
 *    Commits a project (possibly a specific revision) by opening a new browser window
 * </p>
 */
public class ProjectRevisionCommiter {

    private final AvailableProject project;

    private final RevisionNumber revisionNumber;

    private final CommitData commitData;

    /**
     * Constructs a ProjectRevisionCommit for the specified project, revision and project format.
     *
     * @param project The available project id.  Not {@code null}.
     * @param revisionNumber The revision to download.  Not {@code null}.
     * @param commitData The format that the project should be downloaded in.  Not {@code null}.
     * @throws  NullPointerException if any parameters are {@code null}.
     */
    public ProjectRevisionCommiter(AvailableProject project, RevisionNumber revisionNumber, CommitData commitData) {
        this.project = checkNotNull(project);
        this.revisionNumber = checkNotNull(revisionNumber);
        this.commitData = checkNotNull(commitData);
    }

    /**
     * Causes a new browser window to be opened which will commit the specified project revision in the specified
     * format.
     */
    public void commit(String token) {
        String encodedProjectName = URL.encode(project.getProjectId().getId());
        String encodedRepoURI = URL.encode(project.getRepoURI());
        String encodedPath = URL.encode(commitData.getPath());
        String encodedMessage = URL.encode(commitData.getMessage());
        String baseURL = GWT.getHostPageBaseURL();
        String commitURL = baseURL + "commit?"
                + PROJECT + "=" + encodedProjectName  +
                "&" + REVISION + "=" + revisionNumber.getValue() +
                "&" + REPO_URI + "=" + encodedRepoURI +
                "&" + PERSONAL_ACCESS_TOKEN + "=" + token +
                "&" + BRANCH + "=" + commitData.getBranch() +
                "&" + MESSAGE + "=" + encodedMessage +
                "&" + PATH + "=" + encodedPath +
                "&" + FORMAT + "=" + commitData.getGfe().getExtension();
        Window.open(commitURL, "Commit ontology", "");
    }

}
