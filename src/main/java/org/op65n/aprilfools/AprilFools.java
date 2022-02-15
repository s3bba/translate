package org.op65n.aprilfools;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.op65n.aprilfools.command.CommandTranslate;
import org.op65n.aprilfools.command.CommandTranslateToggle;
import org.op65n.aprilfools.impl.AzureTranslate;
import org.op65n.aprilfools.listener.ChatListener;
import org.op65n.aprilfools.model.Translator;

import java.util.Objects;

public class AprilFools extends Plugin {

    private static AprilFools self = null;

    public static AprilFools self() {
        Objects.requireNonNull(self, String.format("Instance of %s is not yet available", AprilFools.class.getSimpleName()));
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
    }

}
