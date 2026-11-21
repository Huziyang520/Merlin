// Forge config specs backing the NeoForge-style screen: the switch table is declared once in
// ClientOptions/ServerOptions, so the specs are generated from it and values are written back to the
// toml files those options document.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

public final class SpecConfig {
    private static final Map<String, ForgeConfigSpec.ConfigValue<?>> CLIENT_VALUES = new LinkedHashMap<>();
    private static final Map<String, ForgeConfigSpec.ConfigValue<?>> SERVER_VALUES = new LinkedHashMap<>();
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        fill(client, ClientOptions.sections(), ClientOptions::in, CLIENT_VALUES);
        CLIENT_SPEC = client.build();
        ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
        fill(server, ServerOptions.sections(), ServerOptions::in, SERVER_VALUES);
        SERVER_SPEC = server.build();
    }

    private SpecConfig() {
    }

    private static void fill(ForgeConfigSpec.Builder builder, Iterable<String> sections,
            java.util.function.Function<String, java.util.List<ConfigOption>> lookup,
            Map<String, ForgeConfigSpec.ConfigValue<?>> target) {
        for (String section : sections) {
            builder.push(section).translation(ConfigOption.KEY_PREFIX + section);
            for (ConfigOption option : lookup.apply(section)) {
                target.put(option.key(), define(builder, option));
            }
            builder.pop();
        }
    }

    private static ForgeConfigSpec.ConfigValue<?> define(ForgeConfigSpec.Builder builder, ConfigOption option) {
        ForgeConfigSpec.Builder entry = builder.comment(option.key()).translation(option.labelKey());
        return switch (option.kind()) {
            case BOOLEAN -> entry.define(option.key(), option.defaultBoolean());
            case INTEGER -> entry.defineInRange(option.key(), option.defaultInt(), option.intMin(), option.intMax());
            case DECIMAL -> entry.defineInRange(option.key(), option.defaultDouble(), option.min(), option.max());
            default -> entry.define(option.key(), option.defaultValue());
        };
    }

    /** Writes the spec values that differ from the toml file, then reloads the runtime snapshot. */
    public static void applyClient() {
        apply(CLIENT_SPEC, CLIENT_VALUES, ClientOptions.values(), ClientConfig.path(), ClientConfig.defaults(),
                "client");
    }

    public static void applyServer() {
        apply(SERVER_SPEC, SERVER_VALUES, ServerOptions.values(), ServerConfig.path(), ServerConfig.defaults(),
                "server");
    }

    private static void apply(ForgeConfigSpec spec, Map<String, ForgeConfigSpec.ConfigValue<?>> values,
            Map<String, String> current, java.nio.file.Path path, String defaults, String side) {
        if (!spec.isLoaded()) {
            return;
        }
        Map<String, String> changed = new LinkedHashMap<>();
        for (Map.Entry<String, ForgeConfigSpec.ConfigValue<?>> entry : values.entrySet()) {
            String next = String.valueOf(entry.getValue().get());
            if (!next.equals(current.get(entry.getKey()))) {
                changed.put(entry.getKey(), next);
            }
        }
        if (changed.isEmpty()) {
            return;
        }
        TomlFile.rewrite(path, defaults, changed);
        Constants.LOG.info("[MerlinLib] {} switch(es) of the {} file updated from the configuration screen",
                changed.size(), side);
        ConfigManager.reload();
    }
}
