package eu.tib.protege.github.api;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GitHubApiServiceImpl implements GitHubApiService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubApiServiceImpl.class);

    @Override
    public File getFile(String link) {
        logger.info("Going to get file from GitHub: {}", link);

        URL url;
        URLConnection connection;
        File tempFile;

        try {
            url = new URL(link);
            connection = url.openConnection();
            connection.setRequestProperty("X-Requested-With", "Curl");
            tempFile = File.createTempFile("upload-", "");
        } catch (IOException e) {
            throw new GitHubIntegrationException(e.getLocalizedMessage());
        }

        try (InputStream inputStream = connection.getInputStream()) {
            FileUtils.copyToFile(inputStream, tempFile);
        } catch (IOException e) {
            throw new GitHubIntegrationException(e.getLocalizedMessage());
        }

        return tempFile;
    }
}
