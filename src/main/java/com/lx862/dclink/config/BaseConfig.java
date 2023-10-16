package com.lx862.dclink.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BaseConfig {
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir().resolve("dclink");
    protected final Path configFile;

    public BaseConfig(Path configFile) {
        this.configFile = configFile;
    }

    public abstract boolean load();
    public abstract boolean generate();

    public abstract boolean save();

    public void ensureRootFolderExist() {
        if(!Files.exists(CONFIG_ROOT)) {
            try {
                Files.createDirectory(CONFIG_ROOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
