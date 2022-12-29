package com.lx.dclink.util;

import net.minecraft.world.World;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;

public class StringHelper {
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

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String formatDate(long time, String placeholderStr) {
        String timeFormat = "HH:mm:ss";
        if(!placeholderStr.equals("{time}") && placeholderStr.contains("|")) {
            timeFormat = removeLastChar(placeholderStr.split("\\|")[1]);
        }
        return DurationFormatUtils.formatDuration(time, timeFormat);
    }

    public static boolean notValidString(String str) {
        return str == null || str.isBlank();
    }
}
