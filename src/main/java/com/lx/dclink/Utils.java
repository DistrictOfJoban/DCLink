package com.lx.dclink;

import net.minecraft.world.World;
import java.text.DecimalFormat;

public class Utils {
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

    public static String formatFileSize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))  + units[digitGroups];
    }
}
