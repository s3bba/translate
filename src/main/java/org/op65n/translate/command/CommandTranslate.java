package org.op65n.translate.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.op65n.translate.Translate;
import org.op65n.translate.configuration.Messages;
import org.op65n.translate.model.Language;
import org.op65n.translate.model.Translator;

import java.util.Arrays;

public class CommandTranslate extends Command {

    private static final String COMMAND = "translate";
    private static final String PERMISSION = "translate.translate";
    private final Plugin plugin = Translate.self();
    private final ProxyServer proxy = plugin.getProxy();
    private final TaskScheduler scheduler = proxy.getScheduler();
    private final Translator translator;

    public CommandTranslate(final @NotNull Translator translator) {
        super(COMMAND, PERMISSION);

        this.translator = translator;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args == null || args.length < 1) {
            sender.sendMessage(Messages.TRANSLATION_COMMAND_USAGE.asComponents());
            return;
        }

        scheduler.runAsync(plugin, () -> {
            Language lang = Language.ENG;
            int copyFrom = 0;

            if (args.length > 1) {
                final String arg0 = args[0].toUpperCase();
                if (arg0.equals("EN") || arg0.equals("ENG")) copyFrom = 1;

                if (arg0.equals("SI")) {
                    lang = Language.SI;
                    copyFrom = 1;
                }
            }

            final String original = String.join(" ", Arrays.copyOfRange(args, copyFrom, args.length));

            translator.translate(lang, original).ifPresent(translated -> {
                final BaseComponent[] message = Messages.TRANSLATION_COMMAND.asComponents(placeholder -> placeholder.map("message" , translated));
                sender.sendMessage(message);
            });
        });
    }

}
