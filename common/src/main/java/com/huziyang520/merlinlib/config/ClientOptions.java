// The client half of the switch table, in the order client.toml documents it.

package com.huziyang520.merlinlib.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClientOptions {
    private static final List<ConfigOption> ALL = List.of(
            ConfigOption.bool("particles", "limit_enabled", true),
            ConfigOption.number("particles", "limit", 512, 1, 1000000));

    private ClientOptions() {
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

    public static String titleKey() {
        return ConfigOption.KEY_PREFIX + "title";
    }

    public static String noticesKey() {
        return ConfigOption.KEY_PREFIX + "notices.open";
    }

    public static String valueOf(ConfigOption option, Map<String, String> values) {
        return ConfigOption.valueOf(option, values);
    }

    public static Map<String, String> values() {
        return new LinkedHashMap<>(TomlFile.read(ClientConfig.path(), ClientConfig.defaults()).values());
    }
}
