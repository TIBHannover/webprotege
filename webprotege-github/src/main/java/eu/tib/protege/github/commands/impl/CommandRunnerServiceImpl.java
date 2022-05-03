package eu.tib.protege.github.commands.impl;

import eu.tib.protege.github.commands.CommandRunnerService;
import eu.tib.protege.github.exception.CommandRunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class CommandRunnerServiceImpl implements CommandRunnerService {
    private static final Logger logger = LoggerFactory.getLogger(CommandRunnerServiceImpl.class);

    /**
     * @param command the command to be executed
     * @return exit code
     */
    @Override
    public int run(String command) {
        logger.info("Going to run command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
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
