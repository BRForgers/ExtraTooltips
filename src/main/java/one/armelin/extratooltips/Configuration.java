package one.armelin.extratooltips;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {
    @Comment(value = "Whether to show the mod label for vanilla items (e.g. 'Hytale' for items from the base game).")
    public boolean showVanillaModLabel = true;

    @Comment(value = "Whether to show only the last mod label for items overridden by multiple mods. If false, all mod labels will be shown.")
    public boolean showOnlyLastMod = false;

    /**
     * Load configuration from file or create default if it doesn't exist
     * @return Loaded or default configuration
     */
    public static Configuration getConfig(Path configPath) {
        var jankson = Jankson.builder().build();
        Configuration config;

        try {
            Path configFile = configPath.resolve("config.json5");

            // Try to load existing config
            if (Files.exists(configFile)) {
                JsonObject configJson = jankson.load(configFile.toFile());
                config = jankson.fromJson(configJson, Configuration.class);
                ExtraTooltips.LOGGER.atInfo().log("Configuration loaded from: " + configFile);
            } else {
                config = new Configuration();
                ExtraTooltips.LOGGER.atInfo().log("Creating default configuration at: " + configFile);
            }

            // Save/update config file
            Files.writeString(configFile, jankson.toJson(config).toJson(true, true));

        } catch (IOException | SyntaxError e) {
            ExtraTooltips.LOGGER.atSevere().withCause(e).log("Failed to load configuration, using defaults");
            config = new Configuration();
        }

        return config;
    }
}