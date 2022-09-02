package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import edu.stanford.bmir.protege.web.client.download.DownloadSettingsView;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;


/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 31.08.2022
 */
public class CommitSettingsViewImpl extends Composite implements DownloadSettingsView {

    interface DownloadSettingsViewImplUiBinder extends UiBinder<HTMLPanel, CommitSettingsViewImpl> {

    }

    private static DownloadSettingsViewImplUiBinder ourUiBinder = GWT.create(DownloadSettingsViewImplUiBinder.class);

    @UiField
    protected ListBox formatListBox;

    public CommitSettingsViewImpl() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        populateListBox();
    }


    private void populateListBox() {
        for(DownloadFormatExtension extension : DownloadFormatExtension.values()) {
            formatListBox.addItem(extension.getDisplayName());
        }
    }

    @Override
    public DownloadFormatExtension getDownloadFormatExtension() {
        int selIndex = formatListBox.getSelectedIndex();
        if(selIndex == 0) {
            return DownloadFormatExtension.owl;
        }
        else {
            return DownloadFormatExtension.values()[selIndex];
        }
    }

    @Override
    public void setDownloadFormatExtension(DownloadFormatExtension extension) {
        int selIndex = extension.ordinal();
        formatListBox.setSelectedIndex(selIndex);
    }

    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return java.util.Optional.empty();
    }
}