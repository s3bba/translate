package org.op65n.translate.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.op65n.translate.configuration.Configuration;
import org.op65n.translate.configuration.Messages;

public class CommandReload extends Command {

    private static final String COMMAND = "translatereload";
    private static final String PERMISSION = "translate.reload";

    public CommandReload() {
        super(COMMAND, PERMISSION);
    }

    /**
     * Execute this command with the specified sender and arguments.
     *
     * @param sender the executor of this command
     * @param args   arguments used to invoke this command
     */
    @Override
    public void execute(final CommandSender sender, final String[] args) {

        sender.sendMessage(Messages.RELOADED.asComponents(
                placeholders -> placeholders.map("success", String.valueOf(Configuration.clearCached()))
        ));
    }

}
