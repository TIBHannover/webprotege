package edu.stanford.bmir.protege.web.client.token;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.token.GetPersonalAccessTokenAction;
import edu.stanford.bmir.protege.web.shared.token.SetPersonalAccessTokenAction;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static edu.stanford.bmir.protege.web.shared.token.SetPersonalAccessTokenResult.Result.TOKEN_ALREADY_EXISTS;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 06/11/2013
 */
public class ChangePersonalAccessTokenPresenter {

    private final DispatchServiceManager dispatchServiceManager;

    private final LoggedInUserProvider loggedInUserProvider;

    @Nonnull
    private final MessageBox messageBox;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final PersonalAccessTokenEditor view;

    @Nonnull
    private final Messages messages;

    @Inject
    public ChangePersonalAccessTokenPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                                              @Nonnull LoggedInUserProvider loggedInUserProvider,
                                              @Nonnull MessageBox messageBox,
                                              @Nonnull ModalManager modalManager,
                                              @Nonnull PersonalAccessTokenEditor view, @Nonnull Messages messages) {
        this.dispatchServiceManager = dispatchServiceManager;
        this.loggedInUserProvider = loggedInUserProvider;
        this.messageBox = messageBox;
        this.modalManager = modalManager;
        this.view = view;
        this.messages = messages;
    }

    public void changePersonalAccessToken() {
        final UserId userId = loggedInUserProvider.getCurrentUserId();
        if (userId.isGuest()) {
            messageBox.showAlert("You must be logged in to change your email address");
            return;
        }
        ProgressMonitor.get().showProgressMonitor("Retrieving personal access token", "Please wait.");

        dispatchServiceManager.execute(new GetPersonalAccessTokenAction(userId), result -> {
            showDialog(result.getPersonalAccessToken());
            ProgressMonitor.get().hideProgressMonitor();
        });
    }

    private void showDialog(Optional<PersonalAccessToken> personalAccessToken) {
        personalAccessToken.ifPresent(view::setValue);
        final UserId userId = loggedInUserProvider.getCurrentUserId();
        ModalPresenter presenter = modalManager.createPresenter();
        presenter.setTitle(messages.changePersonalAccessToken());
        presenter.setView(view);
        presenter.setEscapeButton(DialogButton.CANCEL);
        presenter.setPrimaryButton(DialogButton.OK);
        presenter.setButtonHandler(DialogButton.OK, closer -> {
            view.getValue().ifPresent(address -> changePersonalAccessToken(userId, address, closer));
            if(!view.getValue().isPresent()) {
                messageBox.showAlert("The specified personal access tokens do not match.");
            }
        });
        modalManager.showModal(presenter);
    }

    private void changePersonalAccessToken(UserId userId, PersonalAccessToken token, ModalCloser closer) {
        dispatchServiceManager.execute(new SetPersonalAccessTokenAction(userId, token.getPersonalAccessToken()),
                                       result -> {
                                           if (result.getResult() == TOKEN_ALREADY_EXISTS) {
                                               messageBox.showMessage("Address already taken",
                                                                      "The personal access token that you have specified is taken by another user.  " +
                                                                              "Please specify a different personal access token.");
                                           }
                                           else {
                                               closer.closeModal();
                                           }
                                       });
    }

}
