package edu.stanford.bmir.protege.web.client.git;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;

import javax.inject.Inject;


/**
 * Author Erhun Giray Tuncay<br>
 * Email giray.tuncay@tib.eu <br>
 * TIB-Leibniz Information Centre for Science and Technology and University Library<br>
 * Date 11.10.2022
 */
public class DeleteBranchSettingsViewImpl extends Composite implements DeleteBranchSettingsView {

    interface DeleteBranchSettingsViewImplUiBinder extends UiBinder<HTMLPanel, DeleteBranchSettingsViewImpl> {

    }

    private static DeleteBranchSettingsViewImplUiBinder ourUiBinder = GWT.create(DeleteBranchSettingsViewImplUiBinder.class);

    @UiField
    protected HTMLPanel htmlPanel;

    @UiField
    protected Label warningLabel;

    @UiField
    protected ListBox branchListBox;

    @Inject
    public DeleteBranchSettingsViewImpl(String repoURI, String token) {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);

        if(token!=null){
            if(!token.isEmpty()){
                htmlPanel.setVisible(true);
                warningLabel.setVisible(false);
                branchListBox.setTitle("The remote branch to be deleted.");
                populateBranchListBox(repoURI, token);

            } else {
                htmlPanel.setVisible(false);
                warningLabel.setVisible(true);
            }
        } else {
            htmlPanel.setVisible(false);
            warningLabel.setVisible(true);
        }

    }

    private void populateBranchListBox(String repoURI, String token){
        String trackerType = null;
        if (repoURI.toLowerCase().startsWith("https://github.com") || repoURI.toLowerCase().startsWith("http://github.com"))
            trackerType = "github.com";
        else if (repoURI.toLowerCase().startsWith("https://gitlab.com") || repoURI.toLowerCase().startsWith("http://gitlab.com"))
            trackerType = "gitlab.com";
        else {
            if(repoURI.toLowerCase().split("https://").length > 1)
                trackerType = repoURI.toLowerCase().split("https://")[1].split("/")[0];
            else if(repoURI.toLowerCase().split("http://").length > 1)
                trackerType = repoURI.toLowerCase().split("http://")[1].split("/")[0];
        }
        if(trackerType.equals("github.com"))
            callGithub(convertRepoURI2CallURL(repoURI, trackerType).replace("http://","https://"),token, trackerType);
        else if (trackerType != null)
            callGitlab(convertRepoURI2CallURL(repoURI, trackerType).replace("http://","https://"),token, trackerType);
        else {
            htmlPanel.setVisible(false);
            warningLabel.setVisible(true);
            warningLabel.setText("Please configure a proper repository URI for the project. ");
        }
    }

    public String convertRepoURI2CallURL(String repoURI, String trackerType){
        if (trackerType.equals("github.com")){
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
        } else if (trackerType.contains(".")){
            String[] parsedRepoUrl = repoURI.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i ==2) {
                    Log.info("trackerType: "+trackerType);
                    Log.info("parsedRepoUrl[i]: "+parsedRepoUrl[i]);
                    if(!trackerType.equals(parsedRepoUrl[i]))
                        return "";
                    sb.append("https://"+trackerType+"/api/v4/projects/");
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
        Log.info("callUrl: "+callUrl+" - "+"token: "+token+" - "+"trackerType: "+trackerType);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);

        requestBuilder.setHeader("Accept", "application/vnd.github+json");
        if(token!=null)
            if(!token.isEmpty())
                requestBuilder.setHeader("Authorization", "Bearer "+token);
        // requestBuilder.setIncludeCredentials(true);

        try {
            Request response = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                        returnResponse(response.getText());
                    } else {
                        boolean warning = true;
                        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
                            if(token !=null)
                                if (!token.isEmpty()){
                                    warning = false;
                                    callGithub(callUrl,"",trackerType);
                                }
                        if(warning)
                            displayGenericWarningMessage("Github ResponseCode("+response.getStatusCode()+") "+returnGitErrorMessage(response.getText())+": ");
                    }
                }

                public void onError(Request request, Throwable exception) {
                    displayGenericWarningMessage("Github Error");
                }

            });

        } catch (RequestException e) {
            displayGenericWarningMessage("Github Exception");
            throw new RuntimeException(e);
        }
        return "";
    }

    public String callGitlab(String callUrl, String token, String trackerType){
        Log.info("callUrl: "+callUrl+" - "+"token: "+token+" - "+"trackerType: "+trackerType);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, callUrl);
        if(token!=null)
            if(!token.isEmpty())
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
                        String branchesUrl = null;
                        if(token!=null){
                            if(!token.isEmpty()){
                                branchesUrl = "https://"+trackerType+"/api/v4/projects/"+id+"/repository/branches?access_token="+token;
                            } else {
                                branchesUrl = "https://"+trackerType+"/api/v4/projects/"+id+"/repository/branches";
                            }
                        } else {
                            branchesUrl = "https://"+trackerType+"/api/v4/projects/"+id+"/repository/branches";
                        }

                        Log.info("branchesUrl: "+branchesUrl);
                        RequestBuilder reqBuild = new RequestBuilder(RequestBuilder.GET, branchesUrl);
                        try {
                            Request res = reqBuild.sendRequest(null, new RequestCallback() {
                                @Override
                                public void onResponseReceived(Request request, Response response) {
                                    if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                                        returnResponse(response.getText());
                                    } else {
                                        boolean warning = true;
                                        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
                                            if(token !=null)
                                                if (!token.isEmpty()){
                                                    warning = false;
                                                    callGitlab(callUrl,"",trackerType);
                                                }
                                        if(warning)
                                            displayGenericWarningMessage("Gitlab 2nd Call ResponseCode("+response.getStatusCode()+") "+returnGitErrorMessage(response.getText())+": ");
                                    }
                                }
                                public void onError(Request request, Throwable exception) {
                                    displayGenericWarningMessage("Gitlab 2nd Call Error: ");
                                    exception.printStackTrace();
                                }
                            });
                        } catch (RequestException e) {
                            displayGenericWarningMessage("Gitlab 2nd Call Exception: ");
                            throw new RuntimeException(e);
                        }
                    } else {
                        boolean warning = true;
                        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
                            if(token !=null)
                                if (!token.isEmpty()){
                                    warning = false;
                                    callGitlab(callUrl,"",trackerType);
                                }
                        if(warning)
                            displayGenericWarningMessage("Gitlab ResponseCode("+response.getStatusCode()+") "+returnGitErrorMessage(response.getText())+": ");
                    }
                }
                public void onError(Request request, Throwable exception) {
                    displayGenericWarningMessage("Gitlab Error: ");
                    exception.printStackTrace();
                }
            });
        } catch (RequestException e) {
            displayGenericWarningMessage("Gitlab Exception: ");
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

    public String returnGitErrorMessage(String result) {
        JSONValue jsonValue = JSONParser.parseStrict(result);
        JSONObject messageObject = jsonValue.isObject();
        return messageObject.get("message").isString().stringValue();
    }

    public void displayGenericWarningMessage(String location){
        htmlPanel.setVisible(false);
        warningLabel.setVisible(true);
        warningLabel.setText(location+"Please check your configuration for a proper repository URI of the project and Personal Access Token of the User. ");
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
    public DeleteRemoteBranchData getDeleteRemoteBranchData(){
        return new DeleteRemoteBranchData(getBranch());
    }

    @Override
    public java.util.Optional<HasRequestFocus> getInitialFocusable() {
        return java.util.Optional.empty();
    }
}