package org.op65n.translate.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.op65n.translate.Translate;
import org.op65n.translate.configuration.Messages;
import org.op65n.translate.model.Language;
import org.op65n.translate.model.Translator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final Set<UUID> TRANSLATION_RECEIVERS = new HashSet<>();
    private static boolean translateMessages = true;

    private final Plugin plugin = Translate.self();
    private final ProxyServer proxy = plugin.getProxy();
    private final TaskScheduler scheduler = proxy.getScheduler();
    private final Translator translator;

    public ChatListener(final @NotNull Translator translator) {
        this.translator = translator;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onChatEvent(final @NotNull ChatEvent event) {
        if (!translateMessages) return;
        if (TRANSLATION_RECEIVERS.size() == 0) return;
        if (event.isCommand()) return;
        if (event.isProxyCommand()) return;

        final ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        final Language lang = TRANSLATION_RECEIVERS.contains(sender.getUniqueId()) ? Language.SI : Language.ENG;

        scheduler.runAsync(plugin, () -> translator.translate(lang, event.getMessage()).ifPresent(translatedString -> {
            final BaseComponent[] translated = Messages.TRANSLATION_CHAT.asComponents(placeholders ->
                    placeholders.map("name", sender.getName()).map("display-name", sender.getDisplayName()).map("message", translatedString)
            );

            sender.getServer().getInfo().getPlayers().stream()
                    .filter(player -> TRANSLATION_RECEIVERS.contains(player.getUniqueId()))
                    .forEach(player -> player.sendMessage(translated));
        }));
    }

    public static @NotNull BaseComponent[] togglePlayer(final @NotNull ProxiedPlayer player) {
        final UUID uuid = player.getUniqueId();

        if (TRANSLATION_RECEIVERS.contains(uuid)) {
            TRANSLATION_RECEIVERS.remove(uuid);
            return Messages.PLAYER_TRANSLATIONS_OFF.asComponents();
        }

        TRANSLATION_RECEIVERS.add(uuid);
        return Messages.PLAYER_TRANSLATIONS_ON.asComponents();
    }

    public static @NotNull BaseComponent[] toggleGlobal() {
        if (translateMessages) {
            translateMessages = false;
            return Messages.GLOBAL_TRANSLATIONS_OFF.asComponents();
        }

        translateMessages = true;
        return Messages.GLOBAL_TRANSLATIONS_ON.asComponents();
    }

}
