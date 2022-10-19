package edu.stanford.bmir.protege.web.server.git;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.access.ApplicationResource;
import edu.stanford.bmir.protege.web.server.access.Subject;
import edu.stanford.bmir.protege.web.server.app.ApplicationNameSupplier;
import edu.stanford.bmir.protege.web.server.inject.UploadsDirectory;
import edu.stanford.bmir.protege.web.server.project.PrefixDeclarationsStore;
import edu.stanford.bmir.protege.web.server.project.ProjectManager;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.server.upload.MaxUploadSizeSupplier;
import edu.stanford.bmir.protege.web.server.util.FileContentsSizeCalculator;
import edu.stanford.bmir.protege.web.server.util.MemoryMonitor;
import edu.stanford.bmir.protege.web.server.util.ZipInputStreamChecker;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.bmir.protege.web.shared.upload.FileUploadResponseAttributes;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import eu.tib.protege.github.commands.GitCommandsService;
import eu.tib.protege.github.commands.impl.GitCommandsServiceImpl;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * A servlet for committing and pushing project ontologies into Github & Gitlab.
 * </p>
 *
 */
@ApplicationSingleton
public class GitFileCommitServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory.getLogger(GitFileCommitServlet.class);

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

    GitCommandsService gitCommandsService = new GitCommandsServiceImpl();
    @AutoFactory
    @Inject
    public GitFileCommitServlet(
            @Provided @Nonnull ProjectManager projectManager,
            @Nonnull AccessManager accessManager,
            @Nonnull ApplicationNameSupplier applicationNameSupplier,
            @Nonnull MaxUploadSizeSupplier maxUploadSizeSupplier,
            @Nonnull @UploadsDirectory File uploadsDirectory,
            @Nonnull PrefixDeclarationsStore prefixDeclarationsStore) {
        this.projectManager = checkNotNull(projectManager);
        this.accessManager = checkNotNull(accessManager);
        this.applicationNameSupplier = checkNotNull(applicationNameSupplier);
        this.maxUploadSizeSupplier = checkNotNull(maxUploadSizeSupplier);
        this.uploadsDirectory = checkNotNull(uploadsDirectory);
        this.prefixDeclarationsStore = checkNotNull(prefixDeclarationsStore);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        logger.info("GitFileCommitServlet: This is doGet method in servlet GitFileCommitServlet!");

        GitCommitParameters commitParameters = new GitCommitParameters(req);
        String repoURI = commitParameters.getRepoURI();
        String institution = "";
        String repo = "";
        String gitlabInstance = "";
        String instancePath = "";

        logger.info("Message: "+commitParameters.getMessage());
        logger.info("repoURI: "+commitParameters.getRepoURI());
        logger.info("Branch: "+commitParameters.getBranch());
        logger.info("New Branch: "+commitParameters.getNewBranch());
        logger.info("Personal Access Token: "+commitParameters.getPersonalAccessToken());

        removeDirectory(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId());

        if(repoURI.startsWith("https://github.com") || repoURI.startsWith("http://github.com")){
            String[] parsedRepoUrl = repoURI.split("/");
            for (int i = 0;i<parsedRepoUrl.length;i++) {
                if (i == 3) {
                    institution = parsedRepoUrl[i];
                }
                if (i ==4)
                    repo = parsedRepoUrl[i];
            }
            gitCommandsService.gitCloneGitHub(commitParameters.getPersonalAccessToken(),
                    institution,repo,uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId());
        } else {
            String temp = repoURI;
            gitlabInstance = temp.split("://")[1].split("/")[0];
            instancePath = temp.split(gitlabInstance+"/")[1];
            gitCommandsService.gitCloneGitlab("oauth2",commitParameters.getPersonalAccessToken(),gitlabInstance, instancePath, uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId());
        }

        gitCommandsService.gitCheckout(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId(), commitParameters.getBranch());

        if(!commitParameters.getNewBranch().isEmpty())
            gitCommandsService.gitCheckoutNewBranch(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId(), commitParameters.getNewBranch());


        WebProtegeSession webProtegeSession = new WebProtegeSessionImpl(req.getSession());
        UserId userId = webProtegeSession.getUserInSession();
        if(!accessManager.hasPermission(Subject.forUser(userId),
                                    ApplicationResource.get(),
                                    BuiltInAction.UPLOAD_PROJECT)) {
            sendErrorMessage(resp, "You do not have permission to upload files to " + applicationNameSupplier.get());
        }

        logger.info("Received upload request from {} at {}",
                    webProtegeSession.getUserInSession(),
                    formatAddr(req));

        resp.setHeader("Content-Type", RESPONSE_MIME_TYPE);

        try {

            List<File> uploadedFiles = writeOntologies(commitParameters.getRequestedRevision(),commitParameters.getProjectId(), commitParameters.getPath(), commitParameters.getImportsPath(), commitParameters.getOntologyName(), commitParameters.getFormat());

            for (File uploadedFile : uploadedFiles){
                long sizeInBytes = uploadedFile.length();
                long computedFileSizeInBytes = computeFileSize(uploadedFile);
                logger.info("File size is {} bytes.  Computed file size is {} bytes.", sizeInBytes, computedFileSizeInBytes);
                if(computedFileSizeInBytes > maxUploadSizeSupplier.get()) {
                    sendFileSizeTooLargeResponse(resp);
                }
                else {
                    logger.info("Stored uploaded file with name {}", uploadedFile.getName());
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    logger.info("resp.getStatus(): " + resp.getStatus());
                    sendSuccessMessage(resp, uploadedFile.getName());
                }
            }

            gitCommandsService.gitAddAll(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId());
            gitCommandsService.gitCommit(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId(), commitParameters.getMessage());
            if(!commitParameters.getNewBranch().isEmpty())
                gitCommandsService.gitPush(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId(), commitParameters.getNewBranch());
            else
                gitCommandsService.gitPush(uploadsDirectory.getAbsolutePath()+"/"+commitParameters.getProjectId().getId(), commitParameters.getBranch());

        } catch (Exception e) {
            logger.info("Commit failed because of an error: {}", e.getMessage(), e);
            sendErrorMessage(resp, "Commit failed");
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

    private long computeFileSize(@Nonnull File uploadedFile) throws IOException {
        FileContentsSizeCalculator sizeCalculator = new FileContentsSizeCalculator(new ZipInputStreamChecker());
        return sizeCalculator.getContentsSize(uploadedFile);
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


    private List<File> writeOntologies(@Nonnull RevisionNumber revisionNumber,
                                              @Nonnull ProjectId projectId,
                                              @Nonnull String path,
                                              @Nonnull String importsPath,
                                              @Nonnull String ontologyName,
                                              @Nonnull CommitFileFormat format) throws IOException, OWLOntologyStorageException {

        if (path.length() >=1)
            if(path.charAt(path.length() -1) != '/')
                path = path + "/";

        if (importsPath.length() >=1)
            if(importsPath.charAt(importsPath.length() -1) != '/')
                importsPath = importsPath + "/";

        // TODO: Separate object
        List<File> ontologies = new ArrayList<File>();
        OWLOntologyManager manager = projectManager.getRevisionManager(projectId).getOntologyManagerForRevision(revisionNumber);
        String baseFolder = uploadsDirectory.getAbsolutePath()+"/"+projectId.getId();
        int count = 0;
        for(var ontology : manager.getOntologies()) {
            count++;
            var documentFormat = format.getDocumentFormat();
            if(documentFormat.isPrefixOWLOntologyFormat()) {
                var prefixDocumentFormat = documentFormat.asPrefixOWLOntologyFormat();
                Map<String, String> prefixes = prefixDeclarationsStore.find(projectId).getPrefixes();
                prefixes.forEach(prefixDocumentFormat::setPrefix);
            }

            StringBuilder importsClosure = new StringBuilder("imports closure: ");
            ontology.getImportsClosure().forEach(x ->importsClosure.append(correctShortForm(x)).append(" ; "));
            logger.info(String.format("shortForm: %s imports closure: %s ", correctShortForm(ontology), importsClosure.toString()));

            File ontologyFile = null;
            if (count==1) {
                if(!path.isEmpty()){
                    String directoryPath = baseFolder + "/" + path;
                    File file = new File(directoryPath);
                    boolean directoriesCreated = file.mkdirs();
                    if(directoriesCreated)
                        logger.info(String.format("%s directories created", directoryPath ));
                }
                if(!ontologyName.isEmpty()){
                    ontologyFile = new File(baseFolder + "/" + path +ontologyName + "." + format.getExtension());
                    logger.info("File: "+baseFolder + "/" + path +ontologyName + "." + format.getExtension());
                } else
                {
                    ontologyFile = new File(baseFolder + "/" + path +correctShortForm(ontology) + "." + format.getExtension());
                    logger.info("File: "+baseFolder + "/" + path +correctShortForm(ontology) + "." + format.getExtension());
                }
            } else {
                if(!importsPath.isEmpty()){
                    String directoryPath = baseFolder + "/" + importsPath;
                    File file = new File(directoryPath);
                    boolean directoriesCreated = file.mkdirs();
                    if(directoriesCreated)
                        logger.info(String.format("%s directories created", directoryPath ));

                    ontologyFile = new File(baseFolder + "/" + importsPath +correctShortForm(ontology) + "." + format.getExtension());
                    logger.info("File: ", baseFolder + "/" + importsPath +correctShortForm(ontology) + "." + format.getExtension());
                } else{
                    ontologyFile = new File(baseFolder + "/" + path +correctShortForm(ontology) + "." + format.getExtension());
                    logger.info("File: "+baseFolder + "/" + path +correctShortForm(ontology) + "." + format.getExtension());
                }

            }

            FileOutputStream fos = new FileOutputStream(ontologyFile);
            ontologies.add(ontologyFile);
            ontology.getOWLOntologyManager().saveOntology(ontology, documentFormat, fos);
            fos.close();
            logMemoryUsage();
            }

        return ontologies;
    }

    private String correctShortForm(OWLOntology ontology){
        var ontologyShortForm = getOntologyShortForm(ontology);
        String ontologyDocumentFileName = ontologyShortForm.replace("http://","").
                replace("https://","").
                replace("ftp://","").
                replace("http:/","").
                replace("https:/","").
                replace("ftp:/","").
                replace("www.","").
                replace(".", "-").
                replace("/","-");
        return ontologyDocumentFileName;
    }

    private void logMemoryUsage() {
        MemoryMonitor memoryMonitor = new MemoryMonitor(logger);
        memoryMonitor.monitorMemoryUsage();
    }

    private String getOntologyShortForm(OWLOntology ontology) {
        return new OntologyIRIShortFormProvider().getShortForm(ontology);
    }

}
