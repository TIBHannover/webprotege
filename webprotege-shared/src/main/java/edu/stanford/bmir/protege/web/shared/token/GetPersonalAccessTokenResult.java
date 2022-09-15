package edu.stanford.bmir.protege.web.shared.token;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 * <p>
 *     The result from a {@link GetPersonalAccessTokenAction}.
 * </p>
 */
public class GetPersonalAccessTokenResult implements Result {

    private UserId userId;

    @Nullable
    private PersonalAccessToken personalAccessToken;

    /**
     * For serialization purposes only
     */
    private GetPersonalAccessTokenResult() {
    }

    /**
     * Constructs a {@link GetPersonalAccessTokenResult} object.
     * @param userId The {@link UserId} of the user that the email address belongs to.  Not {@code null}.
     * @param personalAccessToken The email address of the user identified by the specified {@link UserId}.  Not {@code null}.
     * @throws NullPointerException if any parameter is {@code null}.
     */
    public GetPersonalAccessTokenResult(UserId userId, Optional<PersonalAccessToken> personalAccessToken) {
        this.userId = checkNotNull(userId);
        this.personalAccessToken = checkNotNull(personalAccessToken).orElse(null);
    }

    /**
     * Gets the {@link UserId}.
     * @return The {@link UserId}. Not {@code null}.
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the {@link PersonalAccessToken}.
     *
     * @return The {@link PersonalAccessToken}.  An absent value indicates that the personalAccessToken for the specified user id
     * does not exist.  Not {@code null}.
     */
    public Optional<PersonalAccessToken> getPersonalAccessToken() {
        return Optional.ofNullable(personalAccessToken);
    }


}
