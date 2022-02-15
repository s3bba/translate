package org.op65n.translate.impl;

import com.google.gson.*;
import com.squareup.okhttp.*;
import org.jetbrains.annotations.NotNull;
import org.op65n.translate.configuration.Configuration;
import org.op65n.translate.model.Language;
import org.op65n.translate.model.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlParseResult;

import java.util.*;

public class AzureTranslate implements Translator {

    private static final Logger log = LoggerFactory.getLogger(AzureTranslate.class);

    private final String subscriptionKey;
    private final String location;

    private final Map<Language, HttpUrl> endpoints = new HashMap<>(Language.values().length);
    private final OkHttpClient client = new OkHttpClient();

    public AzureTranslate() {
        final Optional<TomlParseResult> optional = Configuration.result();
        if (optional.isEmpty()) throw new RuntimeException("Cannot use AzureTranslate without config present!");

        final TomlParseResult result = optional.get();
        this.subscriptionKey = result.getString("subscription-key", () -> null);
        this.location = result.getString("location", () -> null);

        Objects.requireNonNull(subscriptionKey, "Could not read \"subscription-key\" from config!");
        Objects.requireNonNull(location, "Could not read \"location\" from config!");

        Arrays.stream(Language.values()).forEach(
                language -> endpoints.put(language, AzureTranslate.buildEndpoint(language))
        );
    }

    private static @NotNull HttpUrl buildEndpoint(final @NotNull Language language) {
        String lang = "en";

        switch (language) {
            case ENG -> lang = "en";
            case SI -> lang = "sl";
            default -> log.warn("Unsupported translation {} using \"en\" as fallback", language);
        }

        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.cognitive.microsofttranslator.com")
                .addPathSegment("/translate")
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("to", lang)
                .build();
    }

    @Override
    public @NotNull Optional<String> translate(final @NotNull Language language, final @NotNull String text) {
        final String payload = "[{\"Text\": \"" + text + "\"}]";

        final Optional<HttpUrl> optional = endpoints.entrySet().stream()
                .filter(entry -> entry.getKey() == language)
                .map(Map.Entry::getValue)
                .findAny();

        if (optional.isEmpty()) {
            log.warn("Unsupported translation: {}", language);
            return Optional.empty();
        }

        final HttpUrl url = optional.get();


        String responseBody;

        // create and send request payload
        try {
            final MediaType mediaType = MediaType.parse("application/json");
            final RequestBody body = RequestBody.create(mediaType, payload);
            final Request request = new Request.Builder().url(url).post(body)
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .addHeader("Ocp-Apim-Subscription-Region", location)
                    .addHeader("Content-type", "application/json")
                    .build();

            final Response response = client.newCall(request).execute();

            responseBody = response.body().string();
        } catch (final @NotNull Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }

        // deserialize received payload
        try {
            final JsonArray responseJsonElement = JsonParser.parseString(responseBody).getAsJsonArray();
            final JsonObject responseScope = responseJsonElement.get(0).getAsJsonObject();

            final JsonArray translationsArray = responseScope.get("translations").getAsJsonArray();
            final JsonObject translationsScope = translationsArray.get(0).getAsJsonObject();

            final JsonElement translationsText = translationsScope.get("text");

            return Optional.ofNullable(translationsText.getAsString());
        } catch (final @NotNull Exception ex) {
            ex.printStackTrace();
            log.error("Could not resolve response: {}", responseBody);
            return Optional.empty();
        }
    }

}
