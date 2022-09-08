package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.download.ProjectDownloadConstants.*;

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
public class ProjectRevisionCommit {

    private final ProjectId projectId;

    private final RevisionNumber revisionNumber;

    private final GithubFormatExtension formatExtension;

    /**
     * Constructs a ProjectRevisionCommit for the specified project, revision and project format.
     *
     * @param projectId The project id.  Not {@code null}.
     * @param revisionNumber The revision to download.  Not {@code null}.
     * @param githubFormatExtension The format that the project should be downloaded in.  Not {@code null}.
     * @throws  NullPointerException if any parameters are {@code null}.
     */
    public ProjectRevisionCommit(ProjectId projectId, RevisionNumber revisionNumber, GithubFormatExtension githubFormatExtension) {
        this.projectId = checkNotNull(projectId);
        this.revisionNumber = checkNotNull(revisionNumber);
        this.formatExtension = checkNotNull(githubFormatExtension);
    }

    /**
     * Causes a new browser window to be opened which will commit the specified project revision in the specified
     * format.
     */
    public void commit() {
        String encodedProjectName = URL.encode(projectId.getId());
        String baseURL = GWT.getHostPageBaseURL();
        String commitURL = baseURL + "commit?"
                + PROJECT + "=" + encodedProjectName  +
                "&" + REVISION + "=" + revisionNumber.getValue() +
                "&" + FORMAT + "=" + formatExtension.getExtension();
        Window.open(commitURL, "Commit ontology", "");
    }

}
