package be.wacken.planner.application;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BiographyText {
    private static final Pattern TAGS = Pattern.compile("<[^>]+>");
    private static final Pattern NUMERIC_ENTITY = Pattern.compile("&#(\\d+);");
    private static final Pattern HEX_ENTITY = Pattern.compile("&#x([0-9a-fA-F]+);");

    private BiographyText() {
    }

    public static Optional<String> readable(Optional<String> biography) {
        if (biography == null || biography.isEmpty()) {
            return Optional.empty();
        }
        String text = biography.get();
        text = text.replaceAll("(?i)<\\s*br\\s*/?\\s*>", "\n");
        text = text.replaceAll("(?i)</\\s*p\\s*>", "\n\n");
        text = text.replaceAll("(?i)<\\s*p[^>]*>", "");
        text = TAGS.matcher(text).replaceAll(" ");
        text = decodeEntities(text);
        text = text
                .replace('\u00a0', ' ')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll(" *\\n *", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
        return text.isBlank() ? Optional.empty() : Optional.of(text);
    }

    private static String decodeEntities(String text) {
        String decoded = text
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
        decoded = decodeNumericEntities(decoded, NUMERIC_ENTITY, 10);
        return decodeNumericEntities(decoded, HEX_ENTITY, 16);
    }

    private static String decodeNumericEntities(String text, Pattern pattern, int radix) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = decodeCodePoint(matcher.group(1), radix);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String decodeCodePoint(String value, int radix) {
        try {
            return new String(Character.toChars(Integer.parseInt(value, radix)));
        } catch (IllegalArgumentException ignored) {
            return "";
        }
    }
}
