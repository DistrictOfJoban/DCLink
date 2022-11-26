package com.lx.dclink.data;

import com.lx.dclink.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Placeholder {
    public final LinkedHashMap<String, String> placeholders;
    public final LinkedHashMap<String, Long> timePlaceholders;

    public Placeholder() {
        this.placeholders = new LinkedHashMap<>();
        this.timePlaceholders = new LinkedHashMap<>();
    }

    public String parse(String original) {
        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            original = original.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
        }

        for(Map.Entry<String, Long> entry : timePlaceholders.entrySet()) {
            Pattern pattern = Pattern.compile("\\{" + entry.getKey() + ".*?}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(original);

            while(matcher.find()) {
                String timePlaceholder = matcher.group();
                String time = Utils.formatDate(entry.getValue(), timePlaceholder);
                original = original.replace(timePlaceholder, time);
            }
        }

        return original;
    }

    public void addPlaceholder(String key, String value) {
        placeholders.put(key, value);
    }

    public void addTimePlaceholder(String key, long ms) {
        timePlaceholders.put(key, ms);
    }

    public void addPlaceholder(String key, int value) {
        placeholders.put(key, String.valueOf(value));
    }
}