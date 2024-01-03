package com.lx862.revoltimpl.data.attachment;

import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Image extends File {
    private int width;
    private int height;

    public Image(String id, String tag, String filename, Metadata metadata, long sizeInBytes) {
        super(id, tag, filename, metadata, sizeInBytes);
    }

    public Image(JsonObject jsonObject) {
        super(jsonObject);
        this.width = metadata.getWidth();
        this.height = metadata.getHeight();
    }

    public Image(File file) {
        super(file);
        if(!file.isImage()) {
            throw new IllegalArgumentException("File metadata states this is not an image!");
        }
        this.width = file.metadata.getWidth();
        this.height = file.metadata.getHeight();
    }

    public BufferedImage toBufferedImage() {
        try {
            URL url = new URL(this.getDownloadUrl());
            return ImageIO.read(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
