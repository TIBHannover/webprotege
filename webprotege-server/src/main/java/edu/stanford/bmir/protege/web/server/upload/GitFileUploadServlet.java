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
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.logging.RequestFormatter.formatAddr;



/**
 * author Nenad Krdzavac
 * email nenad.krdzavac@tib.eu
 * TIB-Leibniz Information Centre for Science and Technology and University Library
 * 27.06.2022
 *
 * <p>
 * A servlet for cloning ontologies from Github and uploading it into Webprotege as ontology project.
 * </p>
 *
 */
@ApplicationSingleton
public class GitFileUploadServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory.getLogger(GitFileUploadServlet.class);

    public static final String TEMP_FILE_PREFIX = "upload-";

    public static final String TEMP_FILE_SUFFIX = "";

    public static final String RESPONSE_MIME_TYPE = "text/html";

    @UploadsDirectory
    @Nonnull
    private final File uploadsDirectory;

    private final AccessManager accessManager;

    private final ApplicationNameSupplier applicationNameSupplier;

    private final MaxUploadSizeSupplier maxUploadSizeSupplier;

    @Inject
    public GitFileUploadServlet(
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
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("GitFileUploadServlet: This is doPost method in servlet GitFileUploadServlet!");

        GitCommandsService gitCommandsService = new GitCommandsServiceImpl();
        gitCommandsService.gitCloneGitHub("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",
                "nenadkrdzavac","test","C:/srv/webprotege/uploads/test");

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

            if(true){

                    if(true){

                      File uploadedFile = createServerSideGitFile();

                        long sizeInBytes = uploadedFile.length();
                        long computedFileSizeInBytes = computeFileSize(uploadedFile);
                        logger.info("File size is {} bytes.  Computed file size is {} bytes.", sizeInBytes, computedFileSizeInBytes);
                        if(computedFileSizeInBytes > maxUploadSizeSupplier.get()) {
                            sendFileSizeTooLargeResponse(resp);
                        }
                        else {
                            logger.info("Stored uploaded file with name {}", uploadedFile.getName());
                            resp.setStatus(HttpServletResponse.SC_CREATED);
                            sendSuccessMessage(resp, uploadedFile.getName());
                        }

                        return;
                    }
//                }

                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not find form file item");

            }
            else {
                logger.info("Bad upload request: POST must be multipart encoding.");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "POST must be multipart encoding.");
            }
//        }
//        catch (FileUploadBase.FileSizeLimitExceededException | FileUploadBase.SizeLimitExceededException e) {
//            sendFileSizeTooLargeResponse(resp);
//        }
//        catch (FileUploadBase.FileUploadIOException | FileUploadBase.IOFileUploadException e) {
//            logger.info("File upload failed because an IOException occurred: {}", e.getMessage(), e);
//            sendErrorMessage(resp, "File upload failed because of an IOException");
//        }
//        catch (FileUploadBase.InvalidContentTypeException e) {
//            logger.info("File upload failed because the content type was invalid: {}", e.getMessage());
//            sendErrorMessage(resp, "File upload failed because the content type is invalid");
//        }
//        catch (FileUploadException e) {
//            logger.info("File upload failed: {}", e.getMessage());
//            sendErrorMessage(resp, "File upload failed");
        } catch (Exception e) {
            logger.info("File upload failed because of an error when trying to write the file item: {}", e.getMessage(), e);
            sendErrorMessage(resp, "File upload failed");
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

    /**
     * Creates a fresh file on the server.
     * @return The file.
     * @throws IOException If there was a problem creating the file.
     */
    private File createServerSideGitFile() throws IOException {

        uploadsDirectory.mkdirs();

        File tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, uploadsDirectory);

        File ontologyFile = new File("C:/srv/webprotege/uploads/test/ontologies/pizza/pizza.ttl");

        FileInputStream in = new FileInputStream(ontologyFile);
        FileOutputStream out = new FileOutputStream(tempFile);

        try {
            int n;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return tempFile;
    }

    private File createServerSideFile() throws IOException {
        uploadsDirectory.mkdirs();
        return File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, uploadsDirectory);
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

}
