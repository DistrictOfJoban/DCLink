package com.lx862.revoltimpl.data.attachment;

import com.google.gson.JsonObject;

public class File {
    private final String id;
    private final String tag;
    private final String filename;
    protected final Metadata metadata;
    private final long size;

    public File(String id, String tag, String filename, Metadata metadata, long sizeInBytes) {
        this.id = id;
        this.tag = tag;
        this.filename = filename;
        this.metadata = metadata;
        this.size = sizeInBytes;
    }

    public File(JsonObject jsonObject) {
        this.id = jsonObject.get("_id").getAsString();
        this.tag = jsonObject.get("tag").getAsString();
        this.filename = jsonObject.get("filename").getAsString();
        this.size = jsonObject.get("size").getAsLong();
        this.metadata = new Metadata(jsonObject);
    }

    public File(File file) {
        this.id = file.id;
        this.tag = file.tag;
        this.filename = file.filename;
        this.size = file.size;
        this.metadata = file.metadata;
    }

    public String getUrl() {
        return "https://autumn.revolt.chat/" + tag + "/" + id;
    }

    public String getDownloadUrl() {
        return "https://autumn.revolt.chat/" + tag + "/download/" + id + "/" + filename;
    }

    public boolean isImage() {
        return metadata.getType().equals("Image");
    }

    public Image toImage() {
        if(isImage()) {
            return new Image(this);
        } else {
            return null;
        }
    }
}
