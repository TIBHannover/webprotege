package edu.stanford.bmir.protege.web.server.token;

import edu.stanford.bmir.protege.web.server.dispatch.ApplicationActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.user.UserDetailsManager;
import edu.stanford.bmir.protege.web.shared.token.SetPersonalAccessTokenAction;
import edu.stanford.bmir.protege.web.shared.token.SetPersonalAccessTokenResult;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static edu.stanford.bmir.protege.web.shared.token.SetPersonalAccessTokenResult.Result.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class SetPersonalAccessTokenActionHandler implements ApplicationActionHandler<SetPersonalAccessTokenAction, SetPersonalAccessTokenResult> {

    private final UserDetailsManager userDetailsManager;

    @Inject
    public SetPersonalAccessTokenActionHandler(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Nonnull
    @Override
    public Class<SetPersonalAccessTokenAction> getActionClass() {
        return SetPersonalAccessTokenAction.class;
    }

    @Nonnull
    @Override
    public RequestValidator getRequestValidator(@Nonnull SetPersonalAccessTokenAction action, @Nonnull RequestContext requestContext) {
        return new SetPersonalAccessTokenRequestValidator(action.getUserId(), requestContext.getUserId());
    }

    @Nonnull
    @Override
    public SetPersonalAccessTokenResult execute(@Nonnull SetPersonalAccessTokenAction action, @Nonnull ExecutionContext executionContext) {
        String emailAddress = action.getPersonalAccessToken();
        Optional<UserId> userId = userDetailsManager.getUserIdByPersonalAccessToken(new PersonalAccessToken(emailAddress));
        if(userId.isPresent()) {
            if(userId.get().equals(action.getUserId())) {
                // Same user, same address
                return new SetPersonalAccessTokenResult(TOKEN_UNCHANGED);
            }
            else {
                // Already exists
                return new SetPersonalAccessTokenResult(TOKEN_ALREADY_EXISTS);
            }
        }
        else {
            userDetailsManager.setToken(action.getUserId(), emailAddress);
            return new SetPersonalAccessTokenResult(TOKEN_CHANGED);
        }
    }
}
