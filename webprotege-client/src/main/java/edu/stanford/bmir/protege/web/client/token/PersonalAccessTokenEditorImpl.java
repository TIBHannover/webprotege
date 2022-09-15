package edu.stanford.bmir.protege.web.client.token;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.user.PersonalAccessToken;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/11/2013
 */
public class PersonalAccessTokenEditorImpl extends Composite implements PersonalAccessTokenEditor {

    interface PersonalAccessTokenEditorImplUiBinder extends UiBinder<HTMLPanel, PersonalAccessTokenEditorImpl> {

    }

    private static PersonalAccessTokenEditorImplUiBinder ourUiBinder = GWT.create(PersonalAccessTokenEditorImplUiBinder.class);

    @UiField
    protected TextBox personalAccessTokenField;

    @UiField
    protected TextBox confirmPersonalAccessTokenField;

    private boolean dirty = false;

    @UiHandler("personalAccessTokenField")
    protected void handlePersonalAccessTokenChanged(ValueChangeEvent<String> evt) {

    }

    @UiHandler("confirmPersonalAccessTokenField")
    protected void handleConfirmPersonalAccessTokenChanged(ValueChangeEvent<String> evt) {

    }

    @Inject
    public PersonalAccessTokenEditorImpl() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> personalAccessTokenField.setFocus(true));
    }

    @Override
    public void setValue(PersonalAccessToken pat) {
        setPersonalAccessTokenValue(pat.getPersonalAccessToken());
    }

    @Override
    public void clearValue() {
        setPersonalAccessTokenValue("");
    }

    private void setPersonalAccessTokenValue(String value) {
        personalAccessTokenField.setText(value);
        confirmPersonalAccessTokenField.setValue(value);
        dirty = false;
    }

    @Override
    public Optional<PersonalAccessToken> getValue() {
        if(isWellFormed()) {
            return Optional.of(new PersonalAccessToken(personalAccessTokenField.getText().trim()));
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<PersonalAccessToken>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isWellFormed() {
        return personalAccessTokenField.getText().trim().equals(confirmPersonalAccessTokenField.getText().trim());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        personalAccessTokenField.setFocus(true);
    }
}
