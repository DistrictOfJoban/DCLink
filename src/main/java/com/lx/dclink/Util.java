package com.lx.dclink;

import net.minecraft.world.World;

public class Util {
    public static String getWorldName(World world) {
        if (world.getRegistryKey() == World.OVERWORLD) {
            return "Overworld";
        }

        if (world.getRegistryKey() == World.NETHER) {
            return "The Nether";
        }

        if (world.getRegistryKey() == World.END) {
            return "The End";
        }

        return world.getRegistryKey().getValue().getPath();
    }
}
