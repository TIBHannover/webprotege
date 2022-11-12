package edu.stanford.bmir.protege.web.server.git;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.app.ApplicationNameSupplier;
import edu.stanford.bmir.protege.web.server.inject.UploadsDirectory;
import edu.stanford.bmir.protege.web.server.project.PrefixDeclarationsStore;
import edu.stanford.bmir.protege.web.server.project.ProjectManager;
import edu.stanford.bmir.protege.web.server.project.UploadedProjectSourcesExtractor;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.server.upload.MaxUploadSizeSupplier;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import eu.tib.protege.github.commands.GitCommandsService;
import eu.tib.protege.github.commands.impl.GitCommandsServiceImpl;
import eu.tib.protege.github.commands.impl.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * author Erhun Giray TUNCAY
 * email giray.tuncay@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology
 * 11.10.2022
 *
 * <p>
 * A servlet for committing and pushing project ontologies into Github & Gitlab.
 * </p>
 *
 */
@ApplicationSingleton
public class GitDeleteBranchServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory.getLogger(GitDeleteBranchServlet.class);

    public static final String RESPONSE_MIME_TYPE = "text/html";

    @UploadsDirectory
    @Nonnull
    private final File uploadsDirectory;

    private final AccessManager accessManager;

    private final ApplicationNameSupplier applicationNameSupplier;

    private final MaxUploadSizeSupplier maxUploadSizeSupplier;

    @Nonnull
    private final PrefixDeclarationsStore prefixDeclarationsStore;

    @Nonnull
    private final ProjectManager projectManager;

    @Nonnull
    private final Provider<UploadedProjectSourcesExtractor> uploadedProjectSourcesExtractorProvider;

    GitCommandsService gitCommandsService = new GitCommandsServiceImpl();
    @AutoFactory
    @Inject
    public GitDeleteBranchServlet(
            @Provided @Nonnull ProjectManager projectManager,
            @Nonnull AccessManager accessManager,
            @Nonnull ApplicationNameSupplier applicationNameSupplier,
            @Nonnull MaxUploadSizeSupplier maxUploadSizeSupplier,
            @Nonnull @UploadsDirectory File uploadsDirectory,
            @Nonnull PrefixDeclarationsStore prefixDeclarationsStore,
            @Nonnull Provider<UploadedProjectSourcesExtractor> uploadedProjectSourcesExtractorProvider) {
        this.projectManager = checkNotNull(projectManager);
        this.accessManager = checkNotNull(accessManager);
        this.applicationNameSupplier = checkNotNull(applicationNameSupplier);
        this.maxUploadSizeSupplier = checkNotNull(maxUploadSizeSupplier);
        this.uploadsDirectory = checkNotNull(uploadsDirectory);
        this.prefixDeclarationsStore = checkNotNull(prefixDeclarationsStore);
        this.uploadedProjectSourcesExtractorProvider = uploadedProjectSourcesExtractorProvider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        logger.info("GitBranchSwitchServlet: This is doGet method in servlet GitFileCommitServlet!");

        GitDeleteBranchParameters deleteParameters = new GitDeleteBranchParameters(req);
        String repoURI = deleteParameters.getRepoURI();
        String institution = "";
        String repo = "";
        String gitlabInstance = "";
        String instancePath = "";
        List<Output> gitOutputs= new ArrayList<Output>();
        String repoDirectory = uploadsDirectory.getAbsolutePath()+"/"+deleteParameters.getProjectId().getId();

        logger.info("repoURI: "+deleteParameters.getRepoURI());
        logger.info("Branch: "+deleteParameters.getBranch());
        logger.info("Personal Access Token: "+deleteParameters.getPersonalAccessToken());

        removeDirectory(repoDirectory);

        WebProtegeSession webProtegeSession = new WebProtegeSessionImpl(req.getSession());
        UserId userId = webProtegeSession.getUserInSession();
        String user = userId.getUserName();
        String branch = deleteParameters.getBranch();

        try {
            if(repoURI.startsWith("https://github.com") || repoURI.startsWith("http://github.com")){
                String[] parsedRepoUrl = repoURI.split("/");
                for (int i = 0;i<parsedRepoUrl.length;i++) {
                    if (i == 3) {
                        institution = parsedRepoUrl[i];
                    }
                    if (i ==4)
                        repo = parsedRepoUrl[i];
                }
                gitOutputs.add(gitCommandsService.gitCloneGitHub(deleteParameters.getPersonalAccessToken(),
                        institution,repo,uploadsDirectory.getAbsolutePath()+"/"+deleteParameters.getProjectId().getId()));
            } else {
                String temp = repoURI;
                gitlabInstance = temp.split("://")[1].split("/")[0];
                instancePath = temp.split(gitlabInstance+"/")[1];
                gitOutputs.add(gitCommandsService.gitCloneGitlab("oauth2",deleteParameters.getPersonalAccessToken(),gitlabInstance, instancePath, uploadsDirectory.getAbsolutePath()+"/"+deleteParameters.getProjectId().getId()));
            }

            gitOutputs.add(gitCommandsService.gitDeleteRemoteBranch(uploadsDirectory.getAbsolutePath()+"/"+deleteParameters.getProjectId().getId(), branch));

            sendGitStatusMessage(resp, gitOutputs, repoDirectory, deleteParameters.getPersonalAccessToken());

        } catch (Exception e) {
            logger.info("Delete branch failed because of an error: {}", e.getMessage(), e);
            writeJSONPairs(resp.getWriter(),new Pair("Delete branch failed due to an exception", e.getMessage()));
            sendGitStatusMessage(resp, gitOutputs, repoDirectory, deleteParameters.getPersonalAccessToken());
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

}
