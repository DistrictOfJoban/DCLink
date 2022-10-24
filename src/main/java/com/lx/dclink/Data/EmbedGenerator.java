package com.lx.dclink.Data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EmbedGenerator {
    public static List<MessageEmbed> fromJson(Placeholder placeholder, JsonArray jsonArray) {
        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            try {
                EmbedBuilder embed = new EmbedBuilder();
                JsonObject json = jsonElement.getAsJsonObject();
                String title = getString(placeholder, "title", json);
                String description = getString(placeholder, "description", json);
                int color = json.has("color") ? json.get("color").getAsInt() : 0;

                if (title != null) embed.setTitle(title);
                if (description != null) embed.setDescription(description);
                if (color != 0) embed.setColor(color);

                if (json.has("footer")) {
                    JsonObject footerJson = json.get("footer").getAsJsonObject();
                    String content = getString(placeholder, "text", footerJson);
                    String iconURL = getString(placeholder, "icon_url", footerJson);
                    embed.setFooter(content, iconURL);
                }

                if (json.has("image")) {
                    JsonObject imageJson = json.get("image").getAsJsonObject();
                    String url = getString(placeholder, "url", imageJson);
                    embed.setImage(url);
                }

                if (json.has("thumbnail")) {
                    JsonObject thumbnailJson = json.get("thumbnail").getAsJsonObject();
                    String url = getString(placeholder, "url", thumbnailJson);
                    embed.setThumbnail(url);
                }

                if(json.has("timestamp")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.zzzX");
                    embed.setTimestamp(simpleDateFormat.parse(json.get("timestamp").getAsString()).toInstant());
                }

                if (json.has("author")) {
                    JsonObject authorJson = json.get("author").getAsJsonObject();
                    String name = getString(placeholder, "name", authorJson);
                    String url = getString(placeholder, "url", authorJson);
                    String iconURL = getString(placeholder, "icon_url", authorJson);
                    embed.setAuthor(name, url, iconURL);
                }

                if (json.has("fields")) {
                    JsonArray fields = json.getAsJsonArray("fields");
                    fields.forEach(fieldElement -> {
                        JsonObject field = fieldElement.getAsJsonObject();
                        String name = getString(placeholder, "name", field);
                        String value = getString(placeholder, "value", field);
                        boolean inline = field.has("inline") && field.get("inline").getAsBoolean();

                        if (name == null || value == null) return;
                        embed.addField(name, value, inline);
                    });
                }

                if (!embed.isEmpty()) embeds.add(embed.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return embeds;
    }

    private static String getString(Placeholder placeholder, String key, JsonObject jsonObject) {
        if(jsonObject.has(key)) {
            String value = jsonObject.get(key).getAsString();
            return placeholder == null ? value : placeholder.parse(value);
        } else {
            return null;
        }
    }
}