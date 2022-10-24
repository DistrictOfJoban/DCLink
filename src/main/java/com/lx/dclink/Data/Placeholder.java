package com.lx.dclink.Data;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Placeholder {
    public final LinkedHashMap<String, String> placeholders;

    public Placeholder() {
        this.placeholders = new LinkedHashMap<>();
    }

    public String parse(String original) {
        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            original = original.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
        }
        return original;
    }

    public void addPlaceholder(String key, String value) {
        placeholders.put(key, value);
    }

    public void addPlaceholder(String key, int value) {
        placeholders.put(key, String.valueOf(value));
    }
}