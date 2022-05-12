package edu.stanford.bmir.protege.web.client.projectmanager;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.project.CreateNewGithubProjectPresenter;
import edu.stanford.bmir.protege.web.client.projectmanager.CreateGithubProjectRequestHandler ;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreateGithubProjectRequestHandlerImpl implements CreateGithubProjectRequestHandler {

    @Nonnull
    private final Provider<CreateNewGithubProjectPresenter> presenterProvider;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final Messages messages;

    @Inject
    public CreateGithubProjectRequestHandlerImpl(@Nonnull Provider<CreateNewGithubProjectPresenter> presenterProvider,
                                           @Nonnull ModalManager modalManager,
                                           @Nonnull Messages messages) {
        this.presenterProvider = checkNotNull(presenterProvider);
        this.modalManager = checkNotNull(modalManager);
        this.messages = checkNotNull(messages);

    }

    @Override
    public void handleGithubProjectRequestHandler() {
        CreateNewGithubProjectPresenter presenter = presenterProvider.get();
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle(messages.sourceGithubRepository());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        DialogButton createGithubProjectButton = DialogButton.get(messages.createProjectFromGithub());
        modalPresenter.setPrimaryButton(createGithubProjectButton);
        modalPresenter.setView(presenter.getView());
        modalPresenter.setButtonHandler(createGithubProjectButton, closer -> {
            presenter.validateAndCreateProject(closer::closeModal);
        });
        modalManager.showModal(modalPresenter);
    }
}