//package edu.stanford.bmir.protege.web.client.repo;
/*
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class RepoMetadataService {

    String responseBody;

    //    private static final Logger logger = LoggerFactory.getLogger(RepoMetadataService.class);
    private final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

        @Override
        public String handleResponse(
                final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
//            logger.info("response status: "+status);
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }

    };

    public String runCallGithub(String callUrl, String token) {
        //   logger.info("Github call to URL: ", callUrl);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(callUrl);
        httpget.addHeader("Authorization", "Bearer " + token.toString());
        try {
            String responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String runCallGitlab(String callUrl, String token) {
        //   logger.info("Gitlab call to URL: ", callUrl);
        StringBuilder responseBody = new StringBuilder();
        try {
            URL url = new URL(callUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("PRIVATE-TOKEN", token);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(http.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    responseBody.append(line);
                }
            }
            http.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseBody.toString();
    }
}*/
