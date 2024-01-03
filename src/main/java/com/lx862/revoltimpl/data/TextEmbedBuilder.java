package com.lx862.revoltimpl.data;

public class TextEmbedBuilder {
    private String title = null;
    private String description = null;
    private String color = null;
    private String url = null;
    private String iconUrl = null;
    private String footer = null;

    public TextEmbedBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TextEmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TextEmbedBuilder setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public TextEmbedBuilder setColor(int color) {
        this.color = String.format("#%06X", (0xFFFFFF & color));
        return this;
    }

    public TextEmbedBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public TextEmbedBuilder setIcon(String url) {
        // Must have title for icon to appear
        if(this.title == null) {
            this.title = "  ";
        }
        this.iconUrl = url;
        return this;
    }

    public TextEmbed build() {
        String finalTitle = title;
        String finalDescription = description;
        if(iconUrl != null && title == null) {
            finalTitle = "  ";
        }
        if(footer != null) {
            finalDescription += "\n" + "$\\footnotesize \\textsf{" + footer + "}$";
        }
        return new TextEmbed(finalTitle, finalDescription, color, url, iconUrl);
    }
}
