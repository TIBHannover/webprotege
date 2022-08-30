package edu.stanford.bmir.protege.web.client.projectmanager;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu<br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date 30.08.2022.
 */
public interface PushProjectRequestHandler {

    /**
     * Handle a request to push the specified project.  The project is identified by its {@link ProjectId}.
     * @param projectId The {@link ProjectId} that identifies the project to be pushed.
     */
    void handlePushProjectRequest(ProjectId projectId);
}
