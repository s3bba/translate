package org.op65n.translate.configuration;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Messages {

    NO_PERMISSION("no-permission", "&cYou don't have permission!"),
    GLOBAL_TRANSLATIONS_ON("global-translations-on", "&aYou activated global translation of chat messages"),
    GLOBAL_TRANSLATIONS_OFF("global-translations-off", "&cYou de-activated global translation of chat messages"),
    PLAYER_TRANSLATIONS_ON("player-translations-on", "&aYou activated translation of chat messages"),
    PLAYER_TRANSLATIONS_OFF("player-translations-off", "&cYou de-activated translation of chat messages"),
    TRANSLATION_CHAT("translation-chat", "&6Translated &8(&r%display-name%&8)&6: &r%message%"),
    TRANSLATION_COMMAND("translation-command", "&6Translated: &r%message%"),
    TRANSLATION_COMMAND_USAGE("translation-command-usage", "&a/translate <EN | SI> <message>"),
    RELOADED("configuration-reloaded", "Configuration cache reloaded, successfully: %success%");

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private final String message;

    Messages(final @NotNull String key, final @NotNull String fallback) {
        final Optional<TomlParseResult> optional = Configuration.result();
        if (optional.isEmpty()) {
            this.message = fallback;
            return;
        }

        final TomlParseResult result = optional.get();
        final TomlTable messages = result.getTable("messages");

        if (messages == null) {
            this.message = fallback;
            return;
        }

        this.message = messages.getString(key, () -> fallback);
    }

    private @NotNull String applyColors(@NotNull String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public @NotNull BaseComponent[] asComponents() {
        final String formattedMessage = this.applyColors(message);
        return new ComponentBuilder(formattedMessage).create();
    }

    public @NotNull BaseComponent[] asComponents(final @NotNull Consumer<Placeholder> consumer) {
        String formattedMessage = message;

        Placeholder placeholder = new Placeholder();
        consumer.accept(placeholder);

        for (Map.Entry<String, String> entry : placeholder.placeholders.entrySet()) {
            String formattedPlaceholder = MessageFormat.format("%{0}%", entry.getKey());
            formattedMessage = formattedMessage.replaceAll(formattedPlaceholder, entry.getValue());
        }

        formattedMessage = this.applyColors(formattedMessage);

        return new ComponentBuilder(formattedMessage).create();
    }

    public static class Placeholder {
        private final Map<String, String> placeholders = new HashMap<>();

        public Placeholder map(final @NotNull String key, final @NotNull String value) {
            placeholders.put(key, value);
            return this;
        }
    }

}
