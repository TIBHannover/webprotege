package edu.stanford.bmir.protege.web.server.token;

import edu.stanford.bmir.protege.web.server.dispatch.*;
import edu.stanford.bmir.protege.web.server.user.UserDetailsManager;
import edu.stanford.bmir.protege.web.shared.token.GetPersonalAccessTokenAction;
import edu.stanford.bmir.protege.web.shared.token.GetPersonalAccessTokenResult;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class GetPersonalAccessTokenActionHandler implements ApplicationActionHandler<GetPersonalAccessTokenAction, GetPersonalAccessTokenResult> {

    private final UserDetailsManager userDetailsManager;

    @Inject
    public GetPersonalAccessTokenActionHandler(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Nonnull
    @Override
    public Class<GetPersonalAccessTokenAction> getActionClass() {
        return GetPersonalAccessTokenAction.class;
    }

    @Nonnull
    @Override
    public RequestValidator getRequestValidator(@Nonnull GetPersonalAccessTokenAction action, @Nonnull RequestContext requestContext) {
        return () -> {
            if(!requestContext.getUserId().isGuest()) {
                return RequestValidationResult.getValid();
            }
            else {
                return RequestValidationResult.getInvalid("Cannot get the email address of the guest user");
            }
        };
    }

    @Nonnull
    @Override
    public GetPersonalAccessTokenResult execute(@Nonnull GetPersonalAccessTokenAction action, @Nonnull ExecutionContext executionContext) {
        Optional<PersonalAccessToken> address = userDetailsManager.getToken(action.getUserId()).map(PersonalAccessToken::new);
        return new GetPersonalAccessTokenResult(action.getUserId(), address);
    }
}
