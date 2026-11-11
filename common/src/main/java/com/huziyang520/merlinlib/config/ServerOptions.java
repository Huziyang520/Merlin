// The server half of the switch table, in the order server.toml documents it.

package com.huziyang520.merlinlib.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ServerOptions {
    private static final List<ConfigOption> ALL = List.of(
            ConfigOption.bool("content", "disable_generated_enchantments", false),
            ConfigOption.bool("notices", "enabled", true));

    private ServerOptions() {
    }

    public static List<ConfigOption> all() {
        return ALL;
    }

    public static List<String> sections() {
        return ConfigOption.sectionsOf(ALL);
    }

    public static List<ConfigOption> in(String section) {
        return ConfigOption.of(ALL, section);
    }

    public static String sectionKey(String section) {
        return ConfigOption.KEY_PREFIX + section;
    }

    public static String valueOf(ConfigOption option, Map<String, String> values) {
        return ConfigOption.valueOf(option, values);
    }

    public static Map<String, String> values() {
        return new LinkedHashMap<>(TomlFile.read(ServerConfig.path(), ServerConfig.defaults()).values());
    }
}
