package edu.stanford.bmir.protege.web.client.perspective;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.form.LanguageMapCurrentLocaleMapper;
import edu.stanford.bmir.protege.web.client.library.msgbox.InputBox;
import edu.stanford.bmir.protege.web.client.uuid.UuidV4Provider;
import edu.stanford.bmir.protege.web.shared.lang.LanguageMap;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveDescriptor;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import com.allen_sauer.gwt.log.client.Log;
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
        Log.debug("trying to create fresh perspective");
        inputBox.showDialog("Enter tab name", false, "", input -> {
            GWT.log("[CreateFreshPerspectiveRequestHandlerImpl] Create perspective with name: " + input);
            Log.debug("[CreateFreshPerspectiveRequestHandlerImpl] Create perspective with name: " + input);
            String trimmedInput = input.trim();
            if(trimmedInput.isEmpty()) {
                Log.debug("am i ampty?");
                return;
            }
            //Log.debug(" check whats happening here " + uuidV4Provider.get());
            PerspectiveId perspectiveId = PerspectiveId.get(trimmedInput);
            Log.debug("lets see whats the perspective Id " + perspectiveId);
            String langTag = localeMapper.getCurrentLang();
            LanguageMap label = LanguageMap.of(langTag, trimmedInput);
            PerspectiveDescriptor perspectiveDescriptor = PerspectiveDescriptor.get(perspectiveId,
                                                                                    label,
                                                                                    true);
            Log.debug("trying to create fresh perspective" + perspectiveId + " : " + perspectiveDescriptor);
            callback.createNewPerspective(perspectiveDescriptor);
        });
    }
}
