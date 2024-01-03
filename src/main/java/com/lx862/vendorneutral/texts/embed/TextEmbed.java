package com.lx862.vendorneutral.texts.embed;

import com.lx862.revoltimpl.data.text.embed.TextEmbedBuilder;
import com.lx862.vendorneutral.VendorNeutralComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class TextEmbed implements VendorNeutralComponent {
    public final String title;
    public final String description;
    public final String footer;
    public final int color;
    public final String url;
    public final String thumbnail;
    public final String imageUrl;
    public final Author author;
    private final List<Field> fields;

    public TextEmbed(String title, String description, int color, String url, String imageUrl, String footer, String thumbnail, Author author, List<Field> fields) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.url = url;
        this.imageUrl = imageUrl;
        this.footer = footer;
        this.thumbnail = thumbnail;
        this.fields = fields;
        this.author = author;
    }

    @Override
    public MessageEmbed toDiscord() {
        EmbedBuilder dcEmbed = new EmbedBuilder();
        dcEmbed.setTitle(this.title, this.url);
        dcEmbed.setDescription(this.description);
        dcEmbed.setColor(this.color);
        dcEmbed.setFooter(this.footer);
        dcEmbed.setThumbnail(this.thumbnail);
        dcEmbed.setImage(this.imageUrl);
        if(author != null) dcEmbed.setAuthor(author.name, author.url, author.iconUrl);
        for(Field field : this.fields) {
            dcEmbed.addField(new MessageEmbed.Field(field.title, field.description, field.inline));
        }
        return dcEmbed.build();
    }

    @Override
    public com.lx862.revoltimpl.data.text.embed.TextEmbed toRevolt() {
        TextEmbedBuilder rvEmbed = new TextEmbedBuilder();

        rvEmbed.setTitle(this.title);
        rvEmbed.setURL(this.url);
        rvEmbed.setDescription(this.description);
        rvEmbed.setColor(this.color);
        rvEmbed.setFooter(this.footer);
        rvEmbed.setThumbnail(this.thumbnail);
        for(Field field : this.fields) {
            String header = "**" + field.title + "**";
            rvEmbed.appendDescription(header + "\n" + field.description);
        }
        return rvEmbed.build();
    }

    public static class Field {
        public final String title;
        public final String description;
        public final boolean inline;

        public Field(String title, String description, boolean inline) {
            this.title = title;
            this.description = description;
            this.inline = inline;
        }
    }

    public static class Author {
        public final String name;
        public final String url;
        public final String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }
    }
}
