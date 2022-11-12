package edu.stanford.bmir.protege.web.server.git;

import edu.stanford.bmir.protege.web.shared.git.ProjectCommitConstants;
import edu.stanford.bmir.protege.web.shared.git.DeleteBranchConstants;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 11.10.2022
 * <p>
 *     Wraps a HttpServletRequest to enable information about a download request to be extracted.
 * </p>
 */
public class GitDeleteBranchParameters {

    private HttpServletRequest request;

    public GitDeleteBranchParameters(HttpServletRequest request) {
        this.request = request;
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

    private String getRawBranchParameter() { return request.getParameter(DeleteBranchConstants.BRANCH);}

    private String getRawRepoURIParameter() { return request.getParameter(DeleteBranchConstants.REPO_URI);}

    private String getRawPersonalAccessTokenParameter() { return request.getParameter(DeleteBranchConstants.PERSONAL_ACCESS_TOKEN);}

    private String getRawRevisionParameter() {
        return request.getParameter(DeleteBranchConstants.REVISION);
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
        return toStringHelper("GitDeleteBranchParameters" )
                .add("projectId", getProjectId())
                .add("revision", getRequestedRevision())
                .add("branch",getBranch())
                .add("repoURI",getRepoURI())
                .add("personalAccessToken",getPersonalAccessToken())
                .toString();
    }
}
