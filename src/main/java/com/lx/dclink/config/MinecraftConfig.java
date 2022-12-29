package com.lx.dclink.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.MinecraftEntry;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MinecraftConfig extends BaseConfig {
    private static MinecraftConfig instance;
    public static List<MinecraftEntry> entries = new ArrayList<>();

    public MinecraftConfig() {
        super(CONFIG_ROOT.resolve("minecraft.json"));
    }

    public static MinecraftConfig getInstance() {
        if(instance == null) {
            instance = new MinecraftConfig();
        }
        return instance;
    }

    @Override
    public boolean load() {
        entries.clear();
        if(!Files.exists(configFile)) {
            boolean saved = save();
            if(saved) {
                return load();
            } else {
                return false;
            }
        }

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(configFile))).getAsJsonArray();
            jsonConfig.forEach(jsonElement -> {
                MinecraftEntry entry = MinecraftEntry.fromJson(jsonElement);
                if(entry != null) {
                    entries.add(entry);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonArray jsonArray = new JsonArray();
        for(MinecraftEntry entry : entries) {
            jsonArray.add(MinecraftEntry.toJson(entry));
        }

        try (Writer writer = new FileWriter(configFile.toString())) {
            gson.toJson(jsonArray, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
