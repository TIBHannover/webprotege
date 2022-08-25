package edu.stanford.bmir.protege.web.shared.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu<br>
 *  <br>
 * Date 25.08.2022.
 */
public interface ProjectCommittedToGithubHandler extends EventHandler {

    void handleProjectCommittedToGithub(ProjectCommittedToGithubEvent event);
}
