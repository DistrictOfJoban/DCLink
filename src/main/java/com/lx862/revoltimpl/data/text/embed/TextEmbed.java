package com.lx862.revoltimpl.data.text.embed;

import com.google.gson.JsonObject;
import com.lx862.dclink.util.StringHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TextEmbed {
    private final String title;
    private final String description;
    private final String color;
    private final String url;
    private final String iconUrl;

    public TextEmbed(String title, String description, String color, String url, String iconUrl) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.url = url;
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getUrl() {
        return url;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if(title != null) {
            jsonObject.addProperty("title", title);
        }
        if(description != null) {
            jsonObject.addProperty("description", description);
        }
        if(color != null) {
            jsonObject.addProperty("colour", color);
        }
        if(url != null) {
            jsonObject.addProperty("url", url);
        }
        if(iconUrl != null) {
            jsonObject.addProperty("icon_url", iconUrl);
        }
        return jsonObject;
    }

    public static TextEmbed fromMessageEmbed(MessageEmbed embed) {
        TextEmbedBuilder embedBuilder = new TextEmbedBuilder();
        String description = "";
        if(embed.getTitle() != null) {
            embedBuilder.setTitle(embed.getTitle());
        }

        if(embed.getDescription() != null) {
            description = embed.getDescription();
        }

        if(embed.getUrl() != null) {
            embedBuilder.setURL(embed.getUrl());
        }

        embedBuilder.setColor(embed.getColorRaw());

        if(embed.getThumbnail() != null) {
            embedBuilder.setThumbnail(embed.getThumbnail().getUrl());
        }
        if(embed.getFooter() != null) {
            embedBuilder.setFooter(embed.getFooter().getText());
        }

        if(!embed.getFields().isEmpty()) {
            for(MessageEmbed.Field field : embed.getFields()) {
                String title = field.getName();
                String text = field.getValue();
                if(title != null && text != null) {
                    description += "\\n" + "**" + title + "**" + "\\n";
                    description += text;
                }
            }
        }

        if(!StringHelper.notValidString(description)) {
            embedBuilder.setDescription(description);
        }

        return embedBuilder.build();
    }
}
