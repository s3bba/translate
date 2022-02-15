package org.op65n.translate.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.op65n.translate.configuration.Messages;
import org.op65n.translate.listener.ChatListener;

public class CommandTranslateToggle extends Command {

    private static final String COMMAND = "translatetoggle";
    private static final String PERMISSION = "translate.toggle";

    public CommandTranslateToggle() {
        super(COMMAND, PERMISSION);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final ProxiedPlayer player)) {
            final BaseComponent[] message = ChatListener.toggleGlobal();
            sender.sendMessage(message);
            return;
        }

        if (!player.hasPermission(PERMISSION)) {
            final BaseComponent[] message = Messages.NO_PERMISSION.asComponents();
            player.sendMessage(message);
            return;
        }

        final BaseComponent[] message = ChatListener.togglePlayer(player);
        sender.sendMessage(message);
    }

}
