package edu.stanford.bmir.protege.web.shared.token;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class GetPersonalAccessTokenAction implements Action<GetPersonalAccessTokenResult> {

    private UserId userId;

    /**
     * For serialization purposes only
     */
    private GetPersonalAccessTokenAction() {
    }

    /**
     * Constructs a {@link GetPersonalAccessTokenAction} object to retrieve the personal access token of a particular user.
     * @param userId The {@link UserId} that identifies the user whose personal access token is to be retrieved.  Not {@code null}.
     * @throws NullPointerException if {@code userId} is {@code null}.
     */
    public GetPersonalAccessTokenAction(UserId userId) {
        this.userId = checkNotNull(userId);
        if(userId.isGuest()) {
            throw new IllegalArgumentException("userId must not be guest");
        }
    }

    /**
     * Gets the {@link UserId} of the user whose email address is to be retrieved.
     * @return The {@link UserId}.  Not {@code null}.
     */
    public UserId getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        return "GetPersonalAccessTokenAction".hashCode() + userId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof GetPersonalAccessTokenAction)) {
            return false;
        }
        GetPersonalAccessTokenAction other = (GetPersonalAccessTokenAction) o;
        return userId.equals(other.userId);
    }
}
