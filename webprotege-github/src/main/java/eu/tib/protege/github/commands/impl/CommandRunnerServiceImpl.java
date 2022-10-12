package eu.tib.protege.github.commands.impl;

import com.google.gwt.http.client.URL;
import eu.tib.protege.github.commands.CommandRunnerService;
import eu.tib.protege.github.exception.CommandRunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommandRunnerServiceImpl implements CommandRunnerService {
    private static final Logger logger = LoggerFactory.getLogger(CommandRunnerServiceImpl.class);

    /**
     * @param command the command to be executed
     * @return exit code
     */
    @Override
    public int run(String command) {

        logger.info("Going to run command: {}", command);

        String[] commandFields = command.split(" ");
        List<String> temp = new ArrayList<String>();

        for (int i = 0; i<commandFields.length; i++){
            if(commandFields[i].contains("%20"))
                temp.add(commandFields[i].replace("%20", " "));
            else
                temp.add(commandFields[i]);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(temp);
        Process process = null;
        int exitCode;
        try {
            process = processBuilder.start();
            process.waitFor();
            exitCode = process.exitValue();
            logger.info("Command: {}, exit code: {}", command, exitCode);

            if (process.exitValue() == 0) {
                String message = getMessage(process.getInputStream());
                logger.info(message);
            } else {
                String message = getMessage(process.getErrorStream());
                logger.error(message);
            }
        } catch (Exception e) {
            throw new CommandRunnerException(
                String.format("An error occurred while running command %s: %s", command, e.getLocalizedMessage())
            );
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return exitCode;
    }

    private String getMessage(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            sb.append((char) c);
        }

        return sb.toString();
    }
}
