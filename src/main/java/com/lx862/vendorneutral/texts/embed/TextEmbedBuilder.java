package com.lx862.vendorneutral.texts.embed;

import java.util.ArrayList;
import java.util.List;

public class TextEmbedBuilder {
    private String title = null;
    private String description = null;
    private int color;
    private String url = null;
    private String iconUrl = null;
    private String imageUrl = null;
    private String footer = null;
    private TextEmbed.Author author = null;
    private final List<TextEmbed.Field> fields = new ArrayList<>();

    public TextEmbedBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TextEmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TextEmbedBuilder addField(String name, String description, boolean inline) {
        this.fields.add(new TextEmbed.Field(name, description, inline));
        return this;
    }

    public TextEmbedBuilder addField(String name, String description) {
        return addField(name, description, false);
    }

    public TextEmbedBuilder setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public TextEmbedBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    public TextEmbedBuilder setAuthor(String name, String url, String iconUrl) {
        this.author = new TextEmbed.Author(name, url, iconUrl);
        return this;
    }

    public TextEmbedBuilder setAuthor(String name, String url) {
        return setAuthor(name, url, null);
    }

    public TextEmbedBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public TextEmbedBuilder setImage(String url) {
        this.imageUrl = url;
        return this;
    }

    public TextEmbedBuilder setThumbnail(String url) {
        this.iconUrl = url;
        return this;
    }

    public TextEmbed build() {
        return new TextEmbed(title, description, color, url, imageUrl, footer, iconUrl, author, fields);
    }
}