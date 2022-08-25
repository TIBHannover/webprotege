package edu.stanford.bmir.protege.web.shared.event;

import com.google.web.bindery.event.shared.Event;
import edu.stanford.bmir.protege.web.shared.project.HasProjectId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 * Date: 25.08.2022.
 */
public class ProjectCommittedToGithubEvent extends WebProtegeEvent<ProjectCommittedToGithubHandler> implements HasProjectId {

    public static final transient Event.Type<ProjectCommittedToGithubHandler> ON_PROJECT_COMMITTED_TO_GITHUB = new Event.Type<>();

    private ProjectId projectId;

    private ProjectCommittedToGithubEvent() {
    }

    public ProjectCommittedToGithubEvent(ProjectId projectId) {
        this.projectId = projectId;
    }

    @Nonnull
    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public Event.Type<ProjectCommittedToGithubHandler> getAssociatedType() {
        return ON_PROJECT_COMMITTED_TO_GITHUB;
    }

    @Override
    protected void dispatch(ProjectCommittedToGithubHandler handler) {
        handler.handleProjectCommittedToGithub(this);
    }
}
