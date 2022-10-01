package edu.stanford.bmir.protege.web.server.git;

import edu.stanford.bmir.protege.web.shared.download.ProjectDownloadConstants;
import edu.stanford.bmir.protege.web.shared.git.ProjectCommitConstants;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.project.ProjectIdFormatException;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/06/2012
 * <p>
 *     Wraps a HttpServletRequest to enable information about a download request to be extracted.
 * </p>
 */
public class GitCommitParameters {

    private HttpServletRequest request;

    public GitCommitParameters(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Determines if this is a request for a project download.
     * @return <code>true</code> if this is a request for a project download and the projectId is specified and the
     * id is well-formed, otherwise <code>false</code>.
     */
    public boolean isProjectCommit() {

        String rawRepoURIParameter = getRawRepoURIParameter();
        String rawProjectNameParameter = getRawProjectNameParameter();
        boolean repoURIParamPresent = rawRepoURIParameter != null;
        if(!repoURIParamPresent) {
            return false;
        }

        // There can be one more control for repoURI to be a real URI.
        // Maybe another control for matching projectId with its own repoURI

        boolean projectParamPresent = rawProjectNameParameter != null;
        if(!projectParamPresent) {
            return false;
        }
        try {
            ProjectId.get(rawProjectNameParameter);
            return true;
        } catch (ProjectIdFormatException e) {
            return false;
        }
    }


    public ProjectId getProjectId() {
        String projectName = getRawProjectNameParameter();
        if(projectName == null) {
            throw new UnsupportedOperationException("getProjectId can only be called if the request is for a project commit (isProjectCommit() returns true)");
        }
        return ProjectId.get(projectName);
    }

    public String getBranch() {
        String branch = getRawBranchParameter();
        if(branch == null) {
            throw new UnsupportedOperationException("getBranch can only be called if the request includes a branch)");
        }
        return branch;
    }

    public String getMessage() {
        String message = getRawMessageParameter();
        if(message == null) {
            throw new UnsupportedOperationException("getMessage can only be called if the request includes a message)");
        }
        return message;
    }

    public String getRepoURI() {
        String repoURI = getRawRepoURIParameter();
        if(repoURI == null) {
            throw new UnsupportedOperationException("getRepoURI can only be called if the request includes a repo URI)");
        }
        return repoURI;
    }

    public String getPersonalAccessToken() {
        String personalAccessToken = getRawPersonalAccessTokenParameter();
        if (personalAccessToken == null) {
            throw new UnsupportedOperationException("getPersonalAccessToken can only be called if the request includes a personal access token");
        }
        return personalAccessToken;
    }

    public String getPath() {
        String path = getRawPathParameter();
        if (path == null) {
            throw new UnsupportedOperationException("getPath can only be called if the request includes a path");
        }
        return path;
    }

    /**
     * Gets the requested revision number from the request parameters.
     * @return The requested revision.  If no revision in particular has been requested then the revision number
     * that denotes the head revision will be returned.  Also, if the revision number is malformed in the request then
     * the RevisionNumber corresponding the head revision will be returned.
     */
    public RevisionNumber getRequestedRevision() {
        String revisionString = getRawRevisionParameter();
        if(revisionString == null) {
            return RevisionNumber.getHeadRevisionNumber();
        }
        else {
            try {
                long rev = Long.parseLong(revisionString);
                return RevisionNumber.getRevisionNumber(rev);
            }
            catch (NumberFormatException e) {
                // TODO: Log!
                return RevisionNumber.getHeadRevisionNumber();
            }
        }
    }

    /**
     * Gets the requested format
     * @return The ontology format.  Not {@code null}.
     */
    public CommitFileFormat getFormat() {
        String format = getRawFormatParameter();
        return CommitFileFormat.getFileFormatFromParameterName(format);
    }

    private String getRawFormatParameter() {
        return request.getParameter(ProjectCommitConstants.FORMAT);
    }

    private String getRawBranchParameter() { return request.getParameter(ProjectCommitConstants.BRANCH);}

    private String getRawMessageParameter() { return request.getParameter(ProjectCommitConstants.MESSAGE);}

    private String getRawRepoURIParameter() { return request.getParameter(ProjectCommitConstants.REPO_URI);}

    private String getRawPersonalAccessTokenParameter() { return request.getParameter(ProjectCommitConstants.PERSONAL_ACCESS_TOKEN);}

    private String getRawPathParameter() { return request.getParameter(ProjectCommitConstants.PATH);}

    private String getRawRevisionParameter() {
        return request.getParameter(ProjectDownloadConstants.REVISION);
    }



    /**
     * Gets the raw project name request parameter.
     * @return The parameter or <code>null</code> if the parameter was not specified in the request.
     */
    private String getRawProjectNameParameter() {
        return request.getParameter(ProjectCommitConstants.PROJECT);
    }


    @Override
    public String toString() {
        return toStringHelper("GitCommitParameters" )
                .add("projectId", getProjectId())
                .add("revision", getRequestedRevision())
                .add("format", getFormat())
                .add("branch",getBranch())
                .add("message",getMessage())
                .add("repoURI",getRepoURI())
                .add("personalAccessToken",getPersonalAccessToken())
                .toString();
    }
}
