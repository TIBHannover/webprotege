package edu.stanford.bmir.protege.web.shared.project;

import edu.stanford.bmir.protege.web.shared.dispatch.AbstractHasEventListResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectCommittedToGithubEvent;


/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library <br>
 *
 * Date 31.08.2022.
 */
public class PushProjectsToGithubResult extends AbstractHasEventListResult<ProjectCommittedToGithubEvent> {

    private PushProjectsToGithubResult() {
    }

    public PushProjectsToGithubResult(EventList<ProjectCommittedToGithubEvent> eventList) {
        super(eventList);
    }
}
