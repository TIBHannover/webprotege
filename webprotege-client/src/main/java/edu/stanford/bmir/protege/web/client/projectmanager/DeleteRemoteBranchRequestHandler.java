package edu.stanford.bmir.protege.web.client.projectmanager;

import edu.stanford.bmir.protege.web.shared.project.AvailableProject;

/**
 * @author Erhun Giray TUNCAY
 * @email giray.tuncay@tib.eu
 * TIB-Leibniz Information Center for Science and Technology
 */
public interface DeleteRemoteBranchRequestHandler {

    void handleDeleteRemoteBranchRequest(AvailableProject project, String token);
}
