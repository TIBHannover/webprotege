package edu.stanford.bmir.protege.web.client.git;

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
import edu.stanford.bmir.protege.web.shared.git.CommitFormatExtension;

import javax.inject.Inject;


/**
 * Author Erhun Giray Tuncay<br>
 * Email giray.tuncay@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 11.10.2022
 */
public class CommitSettingsViewImpl extends Composite implements CommitSettingsView {

    interface CommitSettingsViewImplUiBinder extends UiBinder<HTMLPanel, CommitSettingsViewImpl> {

    }

    private static CommitSettingsViewImplUiBinder ourUiBinder = GWT.create(CommitSettingsViewImplUiBinder.class);

    @UiField
    protected ListBox branchListBox;

    @UiField
    protected TextBox newBranchTextBox;

    @UiField
    protected ListBox formatListBox;

    @UiField
    protected TextBox messageTextBox;

    @UiField
    protected TextBox pathTextBox;

    @UiField
    protected TextBox importsPathTextBox;

    @UiField
    protected TextBox ontologyNameTextBox;

    @Inject
    public CommitSettingsViewImpl(String repoURI, String token) {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        importsPathTextBox.setTitle("Directory path of the imported ontologies. Unexisting directories will automatically be created.");
        importsPathTextBox.getElement().setPropertyString("placeholder", "src/imports");
        pathTextBox.setTitle("Directory path of the actual ontology. Unexisting directories will automatically be created.");
        pathTextBox.getElement().setPropertyString("placeholder", "src");
        newBranchTextBox.setTitle("Branches from the original branch specified in the dropdown list. Can be left empty. No blanks in text.");
        newBranchTextBox.getElement().setPropertyString("placeholder", "master-2");
        ontologyNameTextBox.setTitle("No blanks and no extension. The file will be created with its name and its selected extension if it doesn't exist.");
        ontologyNameTextBox.getElement().setPropertyString("placeholder", "oais-ip-tbox");
        messageTextBox.setTitle("Message to be displayed in the repo");
        messageTextBox.getElement().setPropertyString("placeholder", "Updated tbox concepts for #9");
        formatListBox.setTitle("The extension of the ontology document to be committed.");
        branchListBox.setTitle("The original branch to commit to if new branch field is left empty.");
        populateBranchListBox(repoURI, token);
        populateFormatListBox();
    }

    private void populateBranchListBox(String repoURI, String token){
        String trackerType = "github";
        if (repoURI.toLowerCase().contains("github"))
            trackerType = "github";
        else if (repoURI.toLowerCase().contains("gitlab"))
            trackerType = "gitlab";
        if(trackerType.equals("github"))
            callGithub(convertRepoURI2CallURL(repoURI, trackerType),token, trackerType);
        else if (trackerType.equals("gitlab"))
            callGitlab(convertRepoURI2CallURL(repoURI, trackerType),token, trackerType);
    }

    public String convertRepoURI2CallURL(String repoURI, String trackerType){
        if (trackerType.equals("github")){
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
            return sb.toString();
        } else if (trackerType.equals("gitlab")) {
            String[] parsedRepoUrl = repoURI.split("/");
            StringBuilder sb = new StringBuilder();
            String gitlabInstance = "gitlab.com";
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i ==2) {
                    gitlabInstance = parsedRepoUrl[i];
                    sb.append("https://"+gitlabInstance+"/api/v4/projects/");
                }

                if (i > 2) {
                    sb.append(parsedRepoUrl[i]);
                }
                if (i > 2 && i <parsedRepoUrl.length - 1)
                    sb.append("%2F");

            }

            return sb.toString();
        } else return "";
    }

    public String callGithub(String callUrl, String token, String trackerType) {
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);

        requestBuilder.setHeader("Accept", "application/vnd.github+json");
        requestBuilder.setHeader("Authorization", "Bearer "+token);
        // requestBuilder.setIncludeCredentials(true);

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

    public String callGitlab(String callUrl, String token, String trackerType){
        String gitlabInstance = "gitlab.com";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);
        requestBuilder.setHeader("Authorization", "Bearer "+token);
        // requestBuilder.setIncludeCredentials(true);
        try {
            Request response = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                        JSONValue jsonValue = JSONParser.parseStrict(response.getText());
                        JSONObject jsonObject = jsonValue.isObject();
                        double id = jsonObject.get("id").isNumber().doubleValue();
                        String branchesUrl = "https://"+gitlabInstance+"/api/v4/projects/"+id+"/repository/branches";
                        RequestBuilder reqBuild = new RequestBuilder(RequestBuilder.GET, branchesUrl);
                        requestBuilder.setHeader("Authorization", "Bearer "+token);
                        try {
                            Request res = reqBuild.sendRequest(null, new RequestCallback() {
                                @Override
                                public void onResponseReceived(Request request, Response response) {
                                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                                        returnResponse(response.getText());
                                    }
                                }
                                public void onError(Request request, Throwable exception) {
                                    exception.printStackTrace();
                                }
                            });
                        } catch (RequestException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                    }
                }
                public void onError(Request request, Throwable exception) {
                    exception.printStackTrace();
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
    public CommitFormatExtension getGithubFormatExtension() {
        int selIndex = formatListBox.getSelectedIndex();
        if(selIndex == 0) {
            return CommitFormatExtension.owl;
        }
        else {
            return CommitFormatExtension.values()[selIndex];
        }
    }

    @Override
    public void setCommitFormatExtension(CommitFormatExtension extension) {
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
    public String getNewBranch() {
        return newBranchTextBox.getValue();
    }

    @Override
    public void setNewBranch(String newBranch) {
        newBranchTextBox.setValue(newBranch);
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
    public String getImportsPath() {return importsPathTextBox.getValue(); }
    @Override
    public void setImportsPath(String path) { importsPathTextBox.setValue(path);}

    @Override
    public String getOntologyName(){ return ontologyNameTextBox.getValue();}
    @Override
    public void setOntologyName(String name){ ontologyNameTextBox.setValue(name);}

    @Override
    public CommitData getCommitData(){
        return new CommitData(getGithubFormatExtension(),getBranch(),getNewBranch(), getMessage(),getPath(), getImportsPath(), getOntologyName());
    }

    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return java.util.Optional.empty();
    }
}