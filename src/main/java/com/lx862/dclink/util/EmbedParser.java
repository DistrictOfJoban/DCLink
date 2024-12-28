package com.lx862.dclink.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lx862.dclink.data.Placeholder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EmbedParser {
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
                    // FIXME
                    String iconURL = getString(placeholder, "icon_url", footerJson);
                    embed.setFooter(content);
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
                    // FIXME
                    //embed.setTimestamp(simpleDateFormat.parse(json.get("timestamp").getAsString()).toInstant());
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

                embeds.add(embed.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return embeds;
    }

    public static JsonArray toJson(MessageEmbed... embeds) {
        JsonArray returnArray = new JsonArray();

        for (MessageEmbed embed : embeds) {
            try {
                JsonObject embedJson = new JsonObject();
                if(embed.getTitle() != null) {
                    embedJson.addProperty("title", embed.getTitle());
                }

                if(embed.getDescription() != null) {
                    embedJson.addProperty("description", embed.getDescription());
                }

                if(embed.getColorRaw() != 0) {
                    embedJson.addProperty("color", embed.getColorRaw());
                }

                if(embed.getFooter() != null) {
                    JsonObject footerJson = new JsonObject();
                    if(embed.getFooter().getText() != null) {
                        footerJson.addProperty("text", embed.getFooter().getText());
                    }
                    if(embed.getFooter().getIconUrl() != null) {
                        footerJson.addProperty("icon_url", embed.getFooter().getIconUrl());
                    }
                    embedJson.add("footer", footerJson);
                }

                if(embed.getImage() != null) {
                    JsonObject imageJson = new JsonObject();
                    if(embed.getImage().getUrl() != null) {
                        imageJson.addProperty("url", embed.getImage().getUrl());
                    }
                    embedJson.add("image", imageJson);
                }

                if(embed.getThumbnail() != null) {
                    JsonObject thumbnailJson = new JsonObject();
                    if(embed.getThumbnail().getUrl() != null) {
                        thumbnailJson.addProperty("url", embed.getThumbnail().getUrl());
                    }
                    embedJson.add("thumbnail", thumbnailJson);
                }

                if(embed.getAuthor() != null) {
                    JsonObject authorJson = new JsonObject();
                    if(embed.getAuthor().getName() != null) {
                        authorJson.addProperty("name", embed.getAuthor().getName());
                    }
                    if(embed.getAuthor().getUrl() != null) {
                        authorJson.addProperty("url", embed.getAuthor().getUrl());
                    }
                    if(embed.getAuthor().getIconUrl() != null) {
                        authorJson.addProperty("icon_url", embed.getAuthor().getIconUrl());
                    }
                    embedJson.add("author", authorJson);
                }

                if(!embed.getFields().isEmpty()) {
                    JsonArray fieldsJson = new JsonArray();
                    for(MessageEmbed.Field field : embed.getFields()) {
                        JsonObject fieldJson = new JsonObject();
                        if(field.getName() != null) {
                            fieldJson.addProperty("name", field.getName());
                        }
                        if(field.getValue() != null) {
                            fieldJson.addProperty("value", field.getValue());
                        }
                        fieldJson.addProperty("inline", field.isInline());

                        fieldsJson.add(fieldJson);
                    }

                    embedJson.add("fields", fieldsJson);
                }

                returnArray.add(embedJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnArray;
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