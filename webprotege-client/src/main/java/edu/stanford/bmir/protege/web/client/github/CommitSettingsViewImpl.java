package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.repo.RepoMetadataService;
import edu.stanford.bmir.protege.web.shared.github.GithubFormatExtension;
import javax.inject.Inject;


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
    // alternatifi changeemailaddresspresenter da loggedinuserprovider ile
    private final String token;

    private final String repoURI;

    private final RepoMetadataService repoMetadataService = new RepoMetadataService();


    @Inject
    public CommitSettingsViewImpl(String repoURI, String token) {
        this.token = token;
        this.repoURI = repoURI;
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        populateListBox();

    }

    private void populateListBox(){

        String[] parsedRepoUrl = repoURI.split("/");
        StringBuilder sb = new StringBuilder();
        String institution = "";
        String user = "";
        for (int i = 0;i<parsedRepoUrl.length;i++) {
            if(i == 2)
                sb.append("api.");
            if (i == 3) {
                sb.append("repos").append("/");
                institution = parsedRepoUrl[i];
            }
            if (i ==4)
                user = parsedRepoUrl[i];
            sb.append(parsedRepoUrl[i]).append("/");
        }
        sb.append("branches");

        repoMetadataService.callGithub(sb.toString(),token);
        String responseBody = repoMetadataService.getResponseBody();
        JSONValue jsonValue = JSONParser.parseStrict(responseBody);
        JSONArray branches = jsonValue.isArray();

        for (int i = 0; i< branches.size();i++){
            final JSONObject branch = branches.get(i).isObject();
            repositoryListBox.addItem(branch.get("name").isString().stringValue());
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