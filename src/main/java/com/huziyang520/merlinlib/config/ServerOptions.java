// The server half of the switch table: the authoritative switches of server.toml, in the order that file documents them.

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

    public static Map<String, String> values() {
        return new LinkedHashMap<>(TomlFile.read(ServerConfig.path(), ServerConfig.defaults()).values());
    }

    public static String valueOf(ConfigOption option, Map<String, String> values) {
        return ConfigOption.valueOf(option, values);
    }
}
