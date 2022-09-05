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
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;


/**
 * Author Nenad Krdzavac<br>
 * Email nenad.krdzavac@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 31.08.2022
 */
public class CommitSettingsViewImpl extends Composite implements CommitSettingsView {

    interface CommitSettingsViewImplUiBinder extends UiBinder<HTMLPanel, CommitSettingsViewImpl> {

    }

    private static CommitSettingsViewImplUiBinder ourUiBinder = GWT.create(CommitSettingsViewImplUiBinder.class);

    @UiField
    protected ListBox repositoryListBox;

    public CommitSettingsViewImpl() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        populateListBox();
    }


    private void populateListBox() {
        for(GithubFormatExtension extension : GithubFormatExtension.values()) {
            repositoryListBox.addItem(extension.getDisplayName());
        }
    }

    @Override
    public GithubFormatExtension getGithubFormatExtension() {
        int selIndex = repositoryListBox.getSelectedIndex();
        if(selIndex == 0) {
            return GithubFormatExtension.owl;
        }
        else {
            return GithubFormatExtension.values()[selIndex];
        }
    }

    @Override
    public void setGithubFormatExtension(GithubFormatExtension extension) {
        int selIndex = extension.ordinal();
        repositoryListBox.setSelectedIndex(selIndex);
    }

    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return java.util.Optional.empty();
    }
}