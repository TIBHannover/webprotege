package edu.stanford.bmir.protege.web.server.upload;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.access.ApplicationResource;
import edu.stanford.bmir.protege.web.server.access.Subject;
import edu.stanford.bmir.protege.web.server.app.ApplicationNameSupplier;
import edu.stanford.bmir.protege.web.server.inject.UploadsDirectory;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.server.util.FileContentsSizeCalculator;
import edu.stanford.bmir.protege.web.server.util.ZipInputStreamChecker;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.upload.FileUploadResponseAttributes;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import eu.tib.protege.github.commands.GitCommandsService;
import eu.tib.protege.github.commands.impl.GitCommandsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.logging.RequestFormatter.formatAddr;



/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 11.10.2022
 *
 * <p>
 * A servlet for cloning ontologies from Github & Gitlab and uploading it into Webprotege as ontology project.
 * </p>
 *
 */
@ApplicationSingleton
public class GitCloneServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory.getLogger(GitCloneServlet.class);

    public static final String RESPONSE_MIME_TYPE = "text/html";

    @UploadsDirectory
    @Nonnull
    private final File uploadsDirectory;

    private final AccessManager accessManager;

    private final ApplicationNameSupplier applicationNameSupplier;

    GitCommandsService gitCommandsService = new GitCommandsServiceImpl();

    @Inject
    public GitCloneServlet(
            @Nonnull AccessManager accessManager,
            @Nonnull ApplicationNameSupplier applicationNameSupplier,
            @Nonnull @UploadsDirectory File uploadsDirectory) {
        this.accessManager = checkNotNull(accessManager);
        this.applicationNameSupplier = checkNotNull(applicationNameSupplier);
        this.uploadsDirectory = checkNotNull(uploadsDirectory);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("GitCloneServlet: This is doGet method in servlet GitCloneServlet!");
        String repoData = req.getParameter("repoData");
        String repoURI = repoData.split("#parse#")[0];
        String personalAccessToken = repoData.split("#parse#")[1];
        String user = repoData.split("#parse#")[2];
        String project = repoData.split("#parse#")[3];
        logger.info("repoURI: "+repoURI);
        logger.info("personalAccessToken: "+personalAccessToken);
        logger.info("User Name: "+user);
        logger.info("Project: "+project);

        String institution = "";
        String repo = "";
        String gitlabInstance = "";
        String instancePath = "";

        removeDirectory(uploadsDirectory.getAbsolutePath()+"/temp-"+user);

        if(repoURI.startsWith("https://github.com") || repoURI.startsWith("http://github.com")){
            String[] parsedRepoUrl = repoURI.split("/");
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i == 3) {
                    institution = parsedRepoUrl[i];
                }
                if (i ==4)
                    repo = parsedRepoUrl[i];
            }
            gitCommandsService.gitCloneGitHub(personalAccessToken,
                    institution,repo,uploadsDirectory.getAbsolutePath()+"/temp-"+user);
        } else {
            String temp = repoURI;
            gitlabInstance = temp.split("://")[1].split("/")[0];
            instancePath = temp.split(gitlabInstance+"/")[1];
            gitCommandsService.gitCloneGitlab("oauth2",personalAccessToken,gitlabInstance, instancePath, uploadsDirectory.getAbsolutePath()+"/temp-"+user);
        }

        WebProtegeSession webProtegeSession = new WebProtegeSessionImpl(req.getSession());
        UserId userId = webProtegeSession.getUserInSession();
        logger.info("Received upload request from {} at {}",
                    webProtegeSession.getUserInSession(),
                    formatAddr(req));

        resp.setHeader("Content-Type", RESPONSE_MIME_TYPE);

        try {

            List<File> files = listFilesIteratively(new File(uploadsDirectory.getAbsolutePath()+"/temp-"+user));

            for (File repoFile : files){
                long sizeInBytes = repoFile.length();
                logger.info("File size is {} bytes.  Computed file size is {} bytes.", sizeInBytes);
                if ((repoFile.getName().toLowerCase().contains(repo.toLowerCase())
                        || repoFile.getName().toLowerCase().contains(project.toLowerCase()))
                        &&
                        (repoFile.getName().contains(".owl")
                                || repoFile.getName().contains(".ttl")
                                || repoFile.getName().contains(".owx")
                                || repoFile.getName().contains(".omn")
                                || repoFile.getName().contains(".ofn"))){
                    logger.info("Stored cloned file with name {}", repoFile.getName());
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    Path copied = Paths.get(uploadsDirectory.getAbsolutePath()+"/"+repoFile.getName());
                    if(Files.exists(copied))
                        Files.delete(copied);
                    Path originalPath = repoFile.toPath();
                    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                    sendSuccessMessage(resp, repoFile.getName());
                    return;
                }

            }

            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not a proper ontology");

        } catch (Exception e) {
            logger.info("Clone failed because of an error when trying to write the file item: {}", e.getMessage(), e);
            sendErrorMessage(resp, "Clone failed");
        }
    }

    private void removeDirectory(String directoryPath){
        Path dir = Paths.get(directoryPath); //path to the directory
        if(Files.exists(dir))
            try {
                Files
                        .walk(dir) // Traverse the file tree in depth-first order
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                System.out.println("Deleting: " + path);
                                Files.delete(path);  //delete each file or directory
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    // Populates a breadth-first ordered file list of the given directory
    public List<File> listFilesIteratively(File root)
    {
        Queue<File> queue = new ArrayDeque<>();
        List<File> temp = new ArrayList<File>();

        queue.add(root);

        while (!queue.isEmpty())
        {
            File current = queue.poll();
            File[] listOfFilesAndDirectory = current.listFiles();
            if (listOfFilesAndDirectory != null)
            {
                for (File file: listOfFilesAndDirectory)
                {
                    if (file.isDirectory()) {
                        queue.add(file);
                    }
                    else {
                        System.out.println(file);
                        temp.add(file);
                    }
                }
            }
        }
        return temp;
    }

    private void sendSuccessMessage(HttpServletResponse response, String fileName) throws IOException {
        PrintWriter writer = response.getWriter();
        writeJSONPairs(writer,
                new Pair(FileUploadResponseAttributes.RESPONSE_TYPE_ATTRIBUTE.name(), FileUploadResponseAttributes.RESPONSE_TYPE_VALUE_UPLOAD_ACCEPTED.name()),
                new Pair(FileUploadResponseAttributes.UPLOAD_FILE_ID.name(), fileName));

    }

    private void sendErrorMessage(HttpServletResponse response, String errorMessage) throws IOException {
        writeJSONPairs(response.getWriter(),
                new Pair(FileUploadResponseAttributes.RESPONSE_TYPE_ATTRIBUTE.name(), FileUploadResponseAttributes.RESPONSE_TYPE_VALUE_UPLOAD_REJECTED.name()),
                new Pair(FileUploadResponseAttributes.UPLOAD_REJECTED_MESSAGE_ATTRIBUTE.name(), errorMessage)
        );

    }

    private void writeJSONPairs(PrintWriter printWriter, Pair ... pairs) {
        printWriter.println("{");
        for(Iterator<Pair> it = Arrays.asList(pairs).iterator(); it.hasNext(); ) {
            Pair pair = it.next();
            String string = pair.getString();
            writeString(printWriter, string);
            printWriter.print(" : ");
            writeString(printWriter, pair.getValue());
            if(it.hasNext()) {
                printWriter.println(",");
            }
            else {
                printWriter.println();
            }
        }
        printWriter.println("}");
        printWriter.flush();
    }

    private void writeString(PrintWriter printWriter, String string) {
        printWriter.print("\"");
        printWriter.print(string);
        printWriter.print("\"");
    }

    private static class Pair {

        private String string;

        private String value;

        private Pair(String string, String value) {
            this.string = string;
            this.value = value;
        }

        public String getString() {
            return string;
        }

        public String getValue() {
            return value;
        }
    }

}
