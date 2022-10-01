package edu.stanford.bmir.protege.web.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.shared.download.DownloadFormatExtension;
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
    protected ListBox branchListBox;

    @UiField
    protected ListBox formatListBox;

    @UiField
    protected TextBox messageTextBox;

    @UiField
    protected TextBox pathTextBox;

    @Inject
    public CommitSettingsViewImpl(String repoURI, String token) {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        populateBranchListBox(repoURI, token);
        populateFormatListBox();

    }

    private void populateBranchListBox(String repoURI, String token){

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
        System.out.println("repoURI: "+sb.toString());
        System.out.println("token: "+token);
        callGithub(sb.toString(),token);
    }

    public String callGithub(String callUrl, String token) {
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);
        requestBuilder.setHeader("Authorization", "Bearer " + token);

        try {
            Request response = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                        returnResponse(response.getText());
                    } else {
                        // Handle the error.  Can get the status text from response.getStatusText()
                    }
                }

                public void onError(Request request, Throwable exception) {
                    // Code omitted for clarity
                }

            });

        } catch (RequestException e) {
            throw new RuntimeException(e);
        }

        return "";

    }

    public void returnResponse(String result) {
        JSONValue jsonValue = JSONParser.parseStrict(result);
        JSONArray branches = jsonValue.isArray();

        for (int i = 0; i< branches.size();i++){
            final JSONObject branch = branches.get(i).isObject();
            branchListBox.addItem(branch.get("name").isString().stringValue());
        }
    }

    private void populateFormatListBox() {
        for(DownloadFormatExtension extension : DownloadFormatExtension.values()) {
            formatListBox.addItem(extension.getDisplayName());
        }
    }

    @Override
    public GithubFormatExtension getGithubFormatExtension() {
        int selIndex = formatListBox.getSelectedIndex();
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
        formatListBox.setSelectedIndex(selIndex);
    }
    @Override
    public String getBranch(){
        return branchListBox.getSelectedValue();
    }
    @Override
    public void setBranch(String branch){
        for(int i = 0; i<branchListBox.getItemCount(); i++){
            if (branchListBox.getItemText(i).equals(branch))
                branchListBox.setSelectedIndex(i);
        }
    }

    @Override
    public void setBranch(int index){
        branchListBox.setSelectedIndex(index);

    }
    @Override
    public String getMessage(){
        return messageTextBox.getValue();
    }
    @Override
    public void setMessage(String message){
        messageTextBox.setValue(message);
    }

    @Override
    public String getPath() {return pathTextBox.getValue(); }
    @Override
    public void setPath(String path) {pathTextBox.setValue(path);}


    @Override
    public CommitData getCommitData(){
        return new CommitData(getGithubFormatExtension(),getBranch(),getMessage(),getPath());
    }


    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return java.util.Optional.empty();
    }
}