package dev.letsgoaway.unison;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class Config {
    public static PlayerRangeType playerRangeType = PlayerRangeType.BEDROCK;
    public static boolean enableArmsOnArmorStands = true;

    public static boolean disableOffhandKeybind = true;

    public static boolean disableNumberKeyInventoryHotkey = true;
    static FileConfiguration config;

    public static void loadConfig() {
        if (!Unison.plugin.getDataFolder().exists() || !Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile().exists()) {
            Unison.plugin.saveResource("config.yml", false);
        }
        config = Unison.plugin.getConfig();
        loadValues();
        Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile().delete();
        saveValues();
    }

    public static void loadValues() {
        if (config.contains("player-range-type", true)) {
            String type = config.getString("player-range-type");
            if (type != null) {
                if (type.equalsIgnoreCase("bedrock")) {
                    playerRangeType = PlayerRangeType.BEDROCK;
                } else if (type.equalsIgnoreCase("java")) {
                    playerRangeType = PlayerRangeType.JAVA;
                } else if (type.equalsIgnoreCase("pocket")) {
                    playerRangeType = PlayerRangeType.POCKET;
                }
            }
        }
        if (config.contains("enable-arms-on-armor-stands", true)) {
            enableArmsOnArmorStands = config.getBoolean("enable-arms-on-armor-stands");
        }
        if (config.contains("disable-offhand-keybind", true)) {
            disableOffhandKeybind = config.getBoolean("disable-offhand-keybind");
        }
        if (config.contains("disable-number-key-inventory-hotkey", true)) {
            disableNumberKeyInventoryHotkey = config.getBoolean("disable-number-key-inventory-hotkey");
        }
    }

    public static void saveValues() {
        config.set("player-range-type", playerRangeType.name().toLowerCase());
        config.set("enable-arms-on-armor-stands", enableArmsOnArmorStands);
        config.set("disable-offhand-keybind", disableOffhandKeybind);
        config.set("disable-number-key-inventory-hotkey", disableNumberKeyInventoryHotkey);
        try {
            config.save(Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile());
        } catch (IOException e) {
            //
        }
    }
}
