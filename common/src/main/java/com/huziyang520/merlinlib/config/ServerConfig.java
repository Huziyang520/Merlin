// Server side switches.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.content.ContentError;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ServerConfig {
    private static final String DEFAULTS = """
            # MerlinLib server configuration.
            # This file is authoritative; the client only displays what the server allows.
            # Changes are picked up by the vanilla /reload command, together with the .json content files.



            [content]
            # Emergency switch: when true, none of the enchantments defined in config/MerlinLib/*.json
            # are generated. Code api registrations are unaffected.
            disable_generated_enchantments = false

            [notices]
            # Show the join notices business mods registered through MerlinLib (MerlinApi.notices()). On by
            # default; turn it off to silence every join notice at once. Takes effect on the next join.
            # The per mod switches are not here: they are the [mods] table of notices.toml, which the
            # "join notices" screen under /merlinlib notices writes for you.
            enabled = true
            """;

    private boolean disableGeneratedEnchantments;
    private boolean noticesEnabled = true;
    private List<ContentError> errors = List.of();

    public static ServerConfig load() {
        return load(ConfigManager.serverView());
    }

    static ServerConfig load(ConfigReader file) {
        ServerConfig config = new ServerConfig();
        config.disableGeneratedEnchantments = file.getBoolean("content.disable_generated_enchantments", false);
        config.noticesEnabled = file.getBoolean("notices.enabled", true);
        config.errors = file.errors();
        return config;
    }

    static Path path() {
        return Path.of("config", Constants.CONFIG_DIRECTORY, "server.toml");
    }

    static String defaults() {
        return DEFAULTS;
    }












    public boolean disableGeneratedEnchantments() {
        return this.disableGeneratedEnchantments;
    }

    public boolean noticesEnabled() {
        return this.noticesEnabled;
    }

    public List<ContentError> errors() {
        return this.errors;
    }
}
