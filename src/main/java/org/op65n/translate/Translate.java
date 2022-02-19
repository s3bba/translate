package org.op65n.translate;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.op65n.translate.command.CommandReload;
import org.op65n.translate.command.CommandTranslate;
import org.op65n.translate.command.CommandTranslateToggle;
import org.op65n.translate.impl.AzureTranslate;
import org.op65n.translate.listener.ChatListener;
import org.op65n.translate.model.Translator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Translate extends Plugin {

    private static Translate self = null;

    private final Set<Listener> listeners = new HashSet<>();
    private final Set<Command> commands = new HashSet<>();

    public static Translate self() {
        Objects.requireNonNull(self, String.format("Instance of %s is not yet available", Translate.class.getSimpleName()));
        return self;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        self = this;

        final PluginManager manager = this.getProxy().getPluginManager();
        final Translator translator = new AzureTranslate();

        listeners.add(new ChatListener(translator));

        commands.add(new CommandTranslateToggle());
        commands.add(new CommandTranslate(translator));
        commands.add(new CommandReload());

        listeners.forEach(listener -> manager.registerListener(this, listener));
        commands.forEach(command -> manager.registerCommand(this, command));
    }

    /**
     * Called when this plugin is disabled.
     */
    @Override
    public void onDisable() {
        super.onDisable();

        final PluginManager manager = this.getProxy().getPluginManager();

        listeners.forEach(manager::unregisterListener);
        commands.forEach(manager::unregisterCommand);
    }
}
