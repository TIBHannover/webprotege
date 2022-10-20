package eu.tib.protege.github.commands;

import eu.tib.protege.github.commands.impl.Output;

public interface CommandRunnerService {
    Output run(String command);
}
