// Rainbow / gradient text for enchantment names and any other player-facing label.
// Colors are per-character, so a name keeps its gradient when the text changes length.

package com.huziyang520.merlinlib.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import java.lang.reflect.Method;
import java.util.Locale;

public final class TextApi {
    /** Default time for one full hue cycle, in milliseconds. */
    public static final long DEFAULT_PERIOD_MS = 5000L;
    /** Default hue distance between two neighbouring characters. */
    public static final float DEFAULT_HUE_STEP = 0.12F;

    private static final String PINYIN_MATCH = "me.towdium.jecharacters.utils.Match";
    private static Method pinyinContains;
    private static boolean pinyinResolved;

    private static final TextApi INSTANCE = new TextApi();

    private TextApi() {
    }

    public static TextApi instance() {
        return INSTANCE;
    }

    /** Animated rainbow: the hue of every character drifts with wall-clock time. */
    public Component rainbow(Component source) {
        return rainbow(source, DEFAULT_PERIOD_MS, DEFAULT_HUE_STEP);
    }

    /** Animated rainbow with an explicit cycle length and hue distance. */
    public Component rainbow(Component source, long periodMs, float hueStep) {
        long period = Math.max(1L, periodMs);
        float phase = (System.currentTimeMillis() % period) / (float) period;
        return colorize(source, (index, length) -> hueToRgb(phase + index * hueStep));
    }

    /** Frozen rainbow: colors do not move over time. */
    public Component rainbowStatic(Component source) {
        return colorize(source, (index, length) -> hueToRgb(index * DEFAULT_HUE_STEP));
    }

    /** Two-color gradient from {@code fromRgb} to {@code toRgb}, frozen. */
    public Component gradient(Component source, int fromRgb, int toRgb) {
        return colorize(source, (index, length) -> lerpColor(fromRgb, toRgb, length <= 1 ? 0.0F : index / (float) (length - 1)));
    }

    /** 0xRRGGBB for a hue in turns (0..1 wraps around). */
    public static int hueToRgb(float hue) {
        float h = (hue % 1.0F + 1.0F) % 1.0F * 6.0F;
        float x = 1.0F - Math.abs(h % 2.0F - 1.0F);
        float red;
        float green;
        float blue;
        switch ((int) h) {
            case 0 -> { red = 1.0F; green = x; blue = 0.0F; }
            case 1 -> { red = x; green = 1.0F; blue = 0.0F; }
            case 2 -> { red = 0.0F; green = 1.0F; blue = x; }
            case 3 -> { red = 0.0F; green = x; blue = 1.0F; }
            case 4 -> { red = x; green = 0.0F; blue = 1.0F; }
            default -> { red = 1.0F; green = 0.0F; blue = x; }
        }
        return pack(red, green, blue);
    }

    /** Pinyin / initial search with a plain case-insensitive fallback. */
    public boolean pinyinMatches(String text, String needle) {
        if (needle == null || needle.isEmpty()) {
            return true;
        }
        String source = text == null ? "" : text;
        Method method = pinyinContainsMethod();
        if (method != null) {
            try {
                Object result = method.invoke(null, source, needle);
                if (result instanceof Boolean matched) {
                    return matched;
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                // Fall through to the plain contains check.
            }
        }
        return source.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    private static synchronized Method pinyinContainsMethod() {
        if (!pinyinResolved) {
            pinyinResolved = true;
            try {
                pinyinContains = Class.forName(PINYIN_MATCH)
                        .getMethod("contains", CharSequence.class, CharSequence.class);
            } catch (ReflectiveOperationException | LinkageError ignored) {
                pinyinContains = null;
            }
        }
        return pinyinContains;
    }

    private static Component colorize(Component source, ColorPicker picker) {
        String text = source.getString();
        MutableComponent result = Component.empty();
        Style base = source.getStyle();
        int length = Math.max(1, text.length());
        for (int index = 0; index < text.length(); index++) {
            TextColor color = TextColor.fromRgb(picker.colorAt(index, length));
            result.append(Component.literal(String.valueOf(text.charAt(index)))
                    .withStyle(base.withColor(color)));
        }
        return result;
    }

    private static int lerpColor(int from, int to, float ratio) {
        int red = Math.round(((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * ratio);
        int green = Math.round(((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * ratio);
        int blue = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * ratio);
        return (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
    }

    private static int pack(float red, float green, float blue) {
        return (clamp(Math.round(red * 255.0F)) << 16)
                | (clamp(Math.round(green * 255.0F)) << 8)
                | clamp(Math.round(blue * 255.0F));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @FunctionalInterface
    private interface ColorPicker {
        int colorAt(int index, int length);
    }
}
