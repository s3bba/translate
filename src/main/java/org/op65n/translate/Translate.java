package org.op65n.translate;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.op65n.translate.command.CommandReload;
import org.op65n.translate.command.CommandTranslate;
import org.op65n.translate.command.CommandTranslateToggle;
import org.op65n.translate.impl.AzureTranslate;
import org.op65n.translate.listener.ChatListener;
import org.op65n.translate.model.Translator;

import java.util.Objects;

public class Translate extends Plugin {

    private static Translate self = null;

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

        manager.registerListener(this, new ChatListener(translator));
        manager.registerCommand(this, new CommandTranslateToggle());
        manager.registerCommand(this, new CommandTranslate(translator));
        manager.registerCommand(this, new CommandReload());
    }

}
