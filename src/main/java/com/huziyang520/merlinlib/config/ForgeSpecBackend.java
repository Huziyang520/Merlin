// The Forge store for MerlinLib's switches: one spec per half, answered through ConfigReader.
// Keys the spec does not declare fall through to the old toml files, so nothing else loses its home.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.content.ContentError;
import com.huziyang520.merlinlib.util.MerlinColor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.common.ForgeConfigSpec;

final class ForgeSpecBackend implements ConfigBackend {
    private static final String KEY_PREFIX = Constants.MOD_ID + ".configuration.";
    private final String name;
    private final String fileName;
    private final String legacyFileName;
    private final Supplier<ConfigReader> fallback;
    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private final java.util.Deque<String> path = new java.util.ArrayDeque<>();
    private final Map<String, ForgeConfigSpec.ConfigValue<?>> values = new LinkedHashMap<>();
    private final Map<String, int[]> ranges = new LinkedHashMap<>();
    private ForgeConfigSpec spec;

    private ForgeSpecBackend(String name, String fileName, String legacyFileName,
                             Supplier<ConfigReader> fallback) {
        this.name = name;
        this.fileName = fileName;
        this.legacyFileName = legacyFileName;
        this.fallback = fallback;
    }

    static ForgeSpecBackend declare(String name, String fileName, String legacyFileName,
                                    Supplier<ConfigReader> fallback, Consumer<ForgeSpecBackend> definitions) {
        ForgeSpecBackend backend = new ForgeSpecBackend(name, fileName, legacyFileName, fallback);
        definitions.accept(backend);
        backend.spec = backend.builder.build();
        return backend;
    }

    ForgeConfigSpec spec() {
        return this.spec;
    }

    String fileName() {
        return this.fileName;
    }

    ForgeSpecBackend push(String section) {
        this.builder.push(section);
        this.path.addLast(section);
        return this;
    }

    ForgeSpecBackend pop() {
        this.builder.pop();
        this.path.removeLast();
        return this;
    }

    private String label(String option) {
        return KEY_PREFIX + full(option);
    }

    ForgeSpecBackend bool(String option, boolean defaultValue, String comment) {
        this.builder.comment(comment);
        this.builder.translation(label(option));
        this.values.put(full(option), this.builder.define(option, defaultValue));
        return this;
    }

    ForgeSpecBackend number(String option, int defaultValue, int min, int max, String comment) {
        this.builder.comment(comment);
        this.builder.translation(label(option));
        this.values.put(full(option), this.builder.defineInRange(option, defaultValue, min, max));
        this.ranges.put(full(option), new int[] {min, max});
        return this;
    }

    private String full(String option) {
        return this.path.isEmpty() ? option : String.join(".", this.path) + "." + option;
    }

    boolean declared(String key) {
        return this.values.containsKey(key) && this.spec.isLoaded();
    }

    @Override
    public ConfigReader view() {
        return new View(this);
    }

    @Override
    public void write(String key, String value) {
        if (!this.spec.isLoaded()) {
            return;
        }
        ForgeConfigSpec.ConfigValue<?> target = this.values.get(key);
        if (target == null) {
            return;
        }
        Object converted = convert(key, target, value);
        if (converted != null) {
            setUnchecked(target, converted);
        }
    }

    @Override
    public void save() {
        if (this.spec.isLoaded()) {
            this.spec.save();
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    private Object convert(String key, ForgeConfigSpec.ConfigValue<?> value, String raw) {
        Object fallback = value.getDefault();
        try {
            if (fallback instanceof Boolean) {
                if ("true".equalsIgnoreCase(raw)) {
                    return Boolean.TRUE;
                }
                if ("false".equalsIgnoreCase(raw)) {
                    return Boolean.FALSE;
                }
                return null;
            }
            if (fallback instanceof Integer) {
                int read = Integer.parseInt(raw.trim());
                int[] range = this.ranges.get(key);
                if (range == null) {
                    return read;
                }
                return Math.max(range[0], Math.min(range[1], read));
            }
            if (fallback instanceof Double) {
                return Double.parseDouble(raw.trim());
            }
            if (fallback instanceof String) {
                return raw;
            }
        } catch (NumberFormatException exception) {
            return null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void setUnchecked(ForgeConfigSpec.ConfigValue<?> value, Object newOne) {
        ((ForgeConfigSpec.ConfigValue<Object>) value).set(newOne);
    }

    private record View(ForgeSpecBackend backend) implements ConfigReader {
        private ForgeConfigSpec.ConfigValue<?> declared(String key) {
            return this.backend.declared(key) ? this.backend.values.get(key) : null;
        }

        @Override
        public boolean getBoolean(String key, boolean fallback) {
            ForgeConfigSpec.ConfigValue<?> value = declared(key);
            return value == null ? this.backend.fallback.get().getBoolean(key, fallback) : (Boolean) value.get();
        }

        @Override
        public int getInt(String key, int fallback, int min, int max) {
            ForgeConfigSpec.ConfigValue<?> value = declared(key);
            if (value == null) {
                return this.backend.fallback.get().getInt(key, fallback, min, max);
            }
            int read = (Integer) value.get();
            return read < min || read > max ? fallback : read;
        }

        @Override
        public double getDouble(String key, double fallback, double min, double max) {
            ForgeConfigSpec.ConfigValue<?> value = declared(key);
            if (value == null) {
                return this.backend.fallback.get().getDouble(key, fallback, min, max);
            }
            double read = (Double) value.get();
            return read < min || read > max ? fallback : read;
        }

        @Override
        public int getColor(String key, int fallback) {
            ForgeConfigSpec.ConfigValue<?> value = declared(key);
            return value == null ? this.backend.fallback.get().getColor(key, fallback)
                    : MerlinColor.parse((String) value.get()).orElse(fallback);
        }

        @Override
        public String getString(String key, String fallback) {
            ForgeConfigSpec.ConfigValue<?> value = declared(key);
            return value == null ? this.backend.fallback.get().getString(key, fallback) : (String) value.get();
        }

        @Override
        public List<ContentError> errors() {
            return List.of();
        }
    }
}
