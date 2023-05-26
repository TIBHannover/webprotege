package edu.stanford.bmir.protege.web.client.perspective;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import edu.stanford.bmir.protege.web.client.form.LanguageMapCurrentLocaleMapper;
import edu.stanford.bmir.protege.web.client.library.msgbox.InputBox;
import edu.stanford.bmir.protege.web.client.uuid.UuidV4Provider;
import edu.stanford.bmir.protege.web.shared.lang.LanguageMap;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveDescriptor;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 13/02/16
 */
public class CreateFreshPerspectiveRequestHandlerImpl implements CreateFreshPerspectiveRequestHandler {

    @Nonnull
    private final InputBox inputBox;

    @Nonnull
    private final UuidV4Provider uuidV4Provider;

    @Nonnull
    private final LanguageMapCurrentLocaleMapper localeMapper;

    @Inject
    public CreateFreshPerspectiveRequestHandlerImpl(@Nonnull InputBox inputBox,
                                                    @Nonnull UuidV4Provider uuidV4Provider,
                                                    @Nonnull LanguageMapCurrentLocaleMapper localeMapper) {
        this.inputBox = checkNotNull(inputBox);
        this.uuidV4Provider = checkNotNull(uuidV4Provider);
        this.localeMapper = checkNotNull(localeMapper);
    }

    @Override
    public void createFreshPerspective(final Callback callback) {
        inputBox.showDialog("Enter tab name", false, "", input -> {
            GWT.log("[CreateFreshPerspectiveRequestHandlerImpl] Create perspective with name: " + input);
            String trimmedInput = input.trim();
            if(trimmedInput.isEmpty()) {
                return;
            }
            String uuid = Document.get().createUniqueId();

            PerspectiveId perspectiveId = PerspectiveId.get(uuid);
            String langTag = localeMapper.getCurrentLang();
            LanguageMap label = LanguageMap.of(langTag, trimmedInput);
            PerspectiveDescriptor perspectiveDescriptor = PerspectiveDescriptor.get(perspectiveId,
                                                                                    label,
                                                                                    true);
            callback.createNewPerspective(perspectiveDescriptor);
        });
    }
}
