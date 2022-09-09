package edu.stanford.bmir.protege.web.client.projectsettings;

import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2 Jul 2018
 */
public interface GeneralSettingsView extends IsWidget {

    void setDisplayName(@Nonnull String displayName);

    @Nonnull
    String getDisplayName();

    void setDescription(@Nonnull String description);

    @Nonnull
    String getDescription();

    void setPersonalAccessToken(@Nonnull String personalAccessToken);

    @Nonnull
    String getPersonalAccessToken();
}
