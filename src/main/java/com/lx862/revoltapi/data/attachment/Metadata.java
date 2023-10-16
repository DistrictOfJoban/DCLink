package com.lx862.revoltapi.data.attachment;

import com.google.gson.JsonObject;

public class Metadata {
    private String type;
    private int width;
    private int height;

    public Metadata(String type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public Metadata(JsonObject jsonObject) {
        if(jsonObject.has("type")) {
            this.type = jsonObject.get("type").getAsString();
        }

        if(jsonObject.has("width")) {
            this.width = jsonObject.get("width").getAsInt();
        }
        if(jsonObject.has("height")) {
            this.height = jsonObject.get("height").getAsInt();
        }
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
