package org.op65n.aprilfools.configuration;

import org.jetbrains.annotations.NotNull;
import org.op65n.aprilfools.AprilFools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Optional;

public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private static final String CONFIGURATION = "configuration.toml";
    private static TomlParseResult result;
    private static long lastLookup = -1;

    public static boolean clearCached() {
        result = null;
        lastLookup = -1;
        final TomlParseResult tomlParseResult = Configuration.load();

        return tomlParseResult != null;
    }

    public static Optional<TomlParseResult> result() {
        final long time = System.currentTimeMillis();

        if (result == null && time - lastLookup > 10000) {
            result = Configuration.load();
            lastLookup = time;
        }

        return Optional.ofNullable(result);
    }

    private static Optional<InputStream> getResource(@NotNull String filename) {
        try {
            URL url = AprilFools.class.getClassLoader().getResource(filename);

            if (url == null) return Optional.empty();

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return Optional.ofNullable(connection.getInputStream());
        } catch (final @NotNull IOException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void copyResource(final @NotNull String resource) {
        final String resourcePath = resource.replace('\\', '/');
        final Optional<InputStream> optional = Configuration.getResource(resourcePath);

        if (optional.isEmpty()) throw new RuntimeException(String.format("Resource %s cannot be found", resourcePath));

        final File dataFolder = AprilFools.self().getDataFolder();
        final File outFile = new File(dataFolder, resourcePath);

        if (outFile.exists()) return;

        final int lastIndex = resourcePath.lastIndexOf('/');
        final File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) outDir.mkdirs();

        try {
            final InputStream in = optional.get();
            final OutputStream out = new FileOutputStream(outFile);
            final byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException ex) {
            log.error("Could not save {} to {}", outFile.getName(), outFile);
            ex.printStackTrace();
        }
    }

    private static TomlParseResult load() {
        Configuration.copyResource(CONFIGURATION);

        try {
            final Path path = Path.of(AprilFools.self().getDataFolder().getAbsolutePath(), CONFIGURATION);
            final TomlParseResult result = Toml.parse(path);
            result.errors().forEach(error -> log.error(error.toString()));

            return result;
        } catch (final @NotNull IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
