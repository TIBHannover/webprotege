package edu.stanford.bmir.protege.web.server.upload;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.access.ApplicationResource;
import edu.stanford.bmir.protege.web.server.access.Subject;
import edu.stanford.bmir.protege.web.server.app.ApplicationNameSupplier;
import edu.stanford.bmir.protege.web.server.inject.UploadsDirectory;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.upload.FileUploadResponseAttributes;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import eu.tib.protege.github.commands.GitCommandsService;
import eu.tib.protege.github.commands.impl.GitCommandsServiceImpl;
import eu.tib.protege.github.commands.impl.Output;
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


    private final MaxUploadSizeSupplier maxUploadSizeSupplier;

    private final ApplicationNameSupplier applicationNameSupplier;

    GitCommandsService gitCommandsService = new GitCommandsServiceImpl();

    @Inject
    public GitCloneServlet(
            @Nonnull AccessManager accessManager,
            @Nonnull ApplicationNameSupplier applicationNameSupplier,
            @Nonnull MaxUploadSizeSupplier maxUploadSizeSupplier,
            @Nonnull @UploadsDirectory File uploadsDirectory) {
        this.accessManager = checkNotNull(accessManager);
        this.applicationNameSupplier = checkNotNull(applicationNameSupplier);
        this.maxUploadSizeSupplier = checkNotNull(maxUploadSizeSupplier);
        this.uploadsDirectory = checkNotNull(uploadsDirectory);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("GitCloneServlet: This is doGet method in servlet GitCloneServlet!");

        Set<String> parameters = new HashSet<String>(req.getParameterMap().keySet());
        String repoURI = "";
        String personalAccessToken = "";
        String user = "";
        String project = "";
        String path = "";
        String branch = "";
        for (String parameter : parameters){
            switch (parameter){
                case "repoURI":
                    repoURI = req.getParameter("repoURI");
                    break;
                case "personalAccessToken":
                    personalAccessToken = req.getParameter("personalAccessToken");
                    break;
                case "user":
                    user = req.getParameter("user");
                    break;
                case "project":
                    project = req.getParameter("project");
                    break;
                case "path":
                    path = req.getParameter("path");
                    break;
                case "branch":
                    branch = req.getParameter("branch");
                    break;
                default:
                    ;
            }
        }

        logger.info("repoURI: "+repoURI);
        logger.info("personalAccessToken: "+personalAccessToken);
        logger.info("User Name: "+user);
        logger.info("Project: "+project);
        logger.info("Path: "+path);
        logger.info("Branch: "+branch);

        String institution = "";
        String repo = "";
        String gitlabInstance = "";
        String instancePath = "";
        List<Output> gitOutputs= new ArrayList<Output>();
        removeDirectory(uploadsDirectory.getAbsolutePath()+"/temp-"+user);

        WebProtegeSession webProtegeSession = new WebProtegeSessionImpl(req.getSession());
        UserId userId = webProtegeSession.getUserInSession();
        if(!accessManager.hasPermission(Subject.forUser(userId),
                ApplicationResource.get(),
                BuiltInAction.UPLOAD_PROJECT)) {
            sendErrorMessage(resp, "You do not have permission to upload files to " + applicationNameSupplier.get());
            return;
        }

        if(repoURI.startsWith("https://github.com") || repoURI.startsWith("http://github.com")){
            String[] parsedRepoUrl = repoURI.split("/");
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i == 3) {
                    institution = parsedRepoUrl[i];
                }
                if (i ==4)
                    repo = parsedRepoUrl[i];
            }
            gitOutputs.add(gitCommandsService.gitCloneGitHub(personalAccessToken,
                    institution,repo,uploadsDirectory.getAbsolutePath()+"/temp-"+user));
        } else {
            String temp = repoURI;
            gitlabInstance = temp.split("://")[1].split("/")[0];
            instancePath = temp.split(gitlabInstance+"/")[1];
            gitOutputs.add(gitCommandsService.gitCloneGitlab("oauth2",personalAccessToken,gitlabInstance, instancePath, uploadsDirectory.getAbsolutePath()+"/temp-"+user));
        }
        if(branch != null)
            if(!branch.isEmpty())
                gitOutputs.add(gitCommandsService.gitCheckout(uploadsDirectory.getAbsolutePath()+"/temp-"+user, branch));

        logger.info("Received upload request from {} at {}",
                    webProtegeSession.getUserInSession(),
                    formatAddr(req));

        resp.setHeader("Content-Type", RESPONSE_MIME_TYPE);

        try {

            if (path != null )
                if (!path.isEmpty())
                    if (path.endsWith(".owl") || path.endsWith(".ttl") || path.endsWith(".owx") || path.endsWith(".omn") || path.endsWith(".ofn")){
                        if(Files.exists(Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path))){
                            long sizeInBytes = Files.size(Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path));
                            logger.info("File size of the specified file is {} bytes.", sizeInBytes);
                            if(sizeInBytes > maxUploadSizeSupplier.get()) {
                                sendFileSizeTooLargeResponse(resp);
                                Files.delete(Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path));
                            } else {
                                logger.info("Stored the user specified file from clone with name {}", Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path).getFileName().toString());
                                resp.setStatus(HttpServletResponse.SC_CREATED);
                                Path copied = Paths.get(uploadsDirectory.getAbsolutePath()+"/"+Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path).getFileName().toString());
                                if(Files.exists(copied))
                                    Files.delete(copied);
                                Files.copy(Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path), copied, StandardCopyOption.REPLACE_EXISTING);
                                sendSuccessMessage(resp, Paths.get(uploadsDirectory.getAbsolutePath()+"/temp-"+user+"/"+path).getFileName().toString());
                                return;
                            }
                        }
                    }

            List<File> files = listFilesIteratively(new File(uploadsDirectory.getAbsolutePath()+"/temp-"+user));

            for (File repoFile : files){
                long sizeInBytes = repoFile.length();
                logger.info("Computed file size of {} is {} bytes.", repoFile.getName(), sizeInBytes);
                if(sizeInBytes > maxUploadSizeSupplier.get()) {
                    sendFileSizeTooLargeResponse(resp);
                    repoFile.delete();
                    continue;
                }

                if ((repoFile.getName().toLowerCase().contains(repo.toLowerCase())
                        || repoFile.getName().toLowerCase().contains(project.toLowerCase()))
                        &&
                        (repoFile.getName().endsWith(".owl")
                                || repoFile.getName().endsWith(".ttl")
                                || repoFile.getName().endsWith(".owx")
                                || repoFile.getName().endsWith(".omn")
                                || repoFile.getName().endsWith(".ofn"))){
                    logger.info("Stored the first matching file from clone with name {}", repoFile.getName());
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

            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load a proper ontology");

        } catch (Exception e) {
            logger.info("Clone failed because of an error when trying to write the file item: {}", e.getMessage(), e);
            writeJSONPairs(resp.getWriter(),new Pair("Clone failed due to an exception", e.getMessage()));
            sendGitStatusMessage(resp, gitOutputs, uploadsDirectory.getAbsolutePath()+"/temp-"+user, personalAccessToken);
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

    private void sendFileSizeTooLargeResponse(HttpServletResponse resp) throws IOException {
        logger.info("File upload failed because the file exceeds the maximum allowed size.");
        sendErrorMessage(resp, String.format("The file that you attempted to upload is too large.  " +
                        "Files (or zipped file contents) must not exceed %d MB.",
                maxUploadSizeSupplier.get() / (1024 * 1024)));
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

    private void sendGitStatusMessage(HttpServletResponse response, List<Output> gitOutputs, String repoDirectory, String token) throws IOException {
        PrintWriter writer = response.getWriter();
        Pair[] pairs = new Pair[gitOutputs.size()];

        for (int i = 0; i<gitOutputs.size();i++){
            pairs[i] = new Pair(String.format("Message of command %s exited with %d exit code", gitOutputs.get(i).getCommand().replace(repoDirectory, "/repo/directory").replace(token,"personal-access-token"),gitOutputs.get(i).getExitCode()),gitOutputs.get(i).getMessage());
        }
        writeJSONPairs(writer,pairs);
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
