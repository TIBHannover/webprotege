package edu.stanford.bmir.protege.web.server.user;

import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/09/15
 */
public interface HasGetUserIdByPersonalAccessToken {

    /**
     * Retrieves a UserId by their email address.
     * @param personalAccessToken The personal access token.  Not {@code null}.
     * @return The UserId that has the specified email address.  An absent value will be returned if there is
     * no user with the specified email address.
     */
    Optional<UserId> getUserIdByPersonalAccessToken(PersonalAccessToken personalAccessToken);
}
