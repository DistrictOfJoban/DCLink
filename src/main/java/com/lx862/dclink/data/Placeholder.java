package com.lx862.dclink.data;

import com.lx862.dclink.util.StringHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Placeholder {
    private final LinkedHashMap<String, String> placeholders;
    private final LinkedHashMap<String, Long> timePlaceholders;

    public Placeholder() {
        this.placeholders = new LinkedHashMap<>();
        this.timePlaceholders = new LinkedHashMap<>();
    }

    public String parse(String original) {
        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            original = original.replaceAll("\\{" + Pattern.quote(entry.getKey()) + "}", Matcher.quoteReplacement(entry.getValue()));
        }

        for(Map.Entry<String, Long> entry : timePlaceholders.entrySet()) {
            Pattern pattern = Pattern.compile("\\{" + Pattern.quote(entry.getKey()) + ".*?}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(original);

            while(matcher.find()) {
                String timePlaceholder = matcher.group();
                String time = StringHelper.formatDate(entry.getValue(), timePlaceholder);
                original = original.replace(timePlaceholder, time);
            }
        }

        return original;
    }

    public void addPlaceholder(String key, String value) {
        placeholders.put(key, value == null ? "" : value);
    }

    public void addPlaceholder(String key, String property, String value) {
        addPlaceholder(key + "." + property, value);
    }

    public void addTimePlaceholder(String key, long ms) {
        timePlaceholders.put(key, ms);
    }
}