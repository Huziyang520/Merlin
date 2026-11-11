// Client only switches: the particle cap, read at the moment it is used.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.content.ContentError;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientConfig {
    private static final String DEFAULTS = """
            # MerlinLib client configuration.
            # Everything here only affects this client and takes effect right after the game reads the file.
            # Content definitions (enchantments, effects, potions) live in the .json files next to this one
            # and are applied on the server side with /reload.

            [general]
            # File layout version. Written by the mod, read only to bring a file from an older build up to the
            # current layout. Client only: the server half carries no layout marker.
            config_version = 3

            [particles]
            # Cap how many particles this client draws. Some paths of the game ask for one particle per point
            # of damage, so a single hit with a very large number can freeze the client while it builds them
            # all - the frame never finishes because it is still making particles. When enabled, at most
            # `limit` particles are drawn per 1/20 second; the rest are dropped before they exist. On by
            # default.
            limit_enabled = true
            # How many particles may be drawn per 1/20 second. 512 is far above what normal play produces
            # (rain, explosions, a busy farm) and still keeps a damage number in the thousands from freezing
            # the client. Lower it if a very large hit still stutters.
            limit = 512
            """;

    private static final int CONFIG_VERSION = 3;
    private boolean particleLimitEnabled = true;
    private int particleLimit = 512;
    private List<ContentError> errors = List.of();

    public static ClientConfig load() {
        return load(ConfigManager.clientView());
    }

    static ClientConfig load(ConfigReader file) {
        ClientConfig config = new ClientConfig();
        if (file instanceof TomlFile toml && migrate(toml)) {
            file = TomlFile.read(path(), DEFAULTS);
        }

        config.particleLimitEnabled = file.getBoolean("particles.limit_enabled", true);
        config.particleLimit = file.getInt("particles.limit", 512, 1, 1000000);
        config.errors = file.errors();
        return config;
    }

    private static boolean migrate(TomlFile file) {
        int version = file.getInt("general.config_version",
                file.getInt("gui.config_version", 0, 0, Integer.MAX_VALUE), 0, Integer.MAX_VALUE);
        if (version >= CONFIG_VERSION) {
            return false;
        }

        Set<String> known = TomlFile.keysOf(DEFAULTS);
        Map<String, String> carried = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : file.values().entrySet()) {
            if (known.contains(entry.getKey()) && !"general.config_version".equals(entry.getKey())) {
                carried.put(entry.getKey(), entry.getValue());
            }
        }
        carried.put("general.config_version", Integer.toString(CONFIG_VERSION));
        TomlFile.rebuild(path(), DEFAULTS, carried);
        Constants.LOG.info("[MerlinLib] client.toml migrated to layout {}", CONFIG_VERSION);
        return true;
    }

    static Path path() {
        return Path.of("config", Constants.CONFIG_DIRECTORY, "client.toml");
    }

    static String defaults() {
        return DEFAULTS;
    }

    public static void write(String key, String value) {
        TomlFile.rewrite(path(), DEFAULTS, Map.of(key, value));
    }

    public boolean particleLimitEnabled() {
        return this.particleLimitEnabled;
    }

    public int particleLimit() {
        return this.particleLimit;
    }

    public List<ContentError> errors() {
        return this.errors;
    }
}
