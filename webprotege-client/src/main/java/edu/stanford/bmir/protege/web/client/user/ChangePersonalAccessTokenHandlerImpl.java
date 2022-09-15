package edu.stanford.bmir.protege.web.client.user;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.token.ChangePersonalAccessTokenPresenter;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class ChangePersonalAccessTokenHandlerImpl implements ChangePersonalAccessTokenHandler {

    private final DispatchServiceManager dispatchServiceManager;

    private final LoggedInUserProvider loggedInUserProvider;

    private final Provider<ChangePersonalAccessTokenPresenter> changePersonalAccessTokenPresenterProvider;

    @Inject
    public ChangePersonalAccessTokenHandlerImpl(DispatchServiceManager dispatchServiceManager,
                                                LoggedInUserProvider loggedInUserProvider,
                                                Provider<ChangePersonalAccessTokenPresenter> changePersonalAccessTokenPresenterProvider) {
        this.dispatchServiceManager = dispatchServiceManager;
        this.loggedInUserProvider = loggedInUserProvider;
        this.changePersonalAccessTokenPresenterProvider = changePersonalAccessTokenPresenterProvider;
    }

    @Override
    public void handleChangePersonalAccessToken() {
        ChangePersonalAccessTokenPresenter presenter = changePersonalAccessTokenPresenterProvider.get();
        presenter.changePersonalAccessToken();
    }
}
