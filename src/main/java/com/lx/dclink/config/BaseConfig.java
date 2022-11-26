package com.lx.dclink.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public abstract class BaseConfig {
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir().resolve("dclink");
}
