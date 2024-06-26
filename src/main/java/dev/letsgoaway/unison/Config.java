package dev.letsgoaway.unison;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class Config {
    public static PlayerRangeType playerRangeType = PlayerRangeType.BEDROCK;
    public static boolean enableArmsOnArmorStands = true;

    public static boolean disableOffhandKeybind = true;

    public static boolean disableNumberKeyInventoryHotkey = true;

    public static boolean bedrockOffhandItemsOnly = true;

    public static boolean bedrockConsumableEffects = true;
    public static boolean blockWhenSneaking = true;
    static FileConfiguration config;

    public static void loadConfig() {
        if (!Unison.plugin.getDataFolder().exists() || !Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile().exists()) {
            Unison.plugin.saveResource("config.yml", false);
        }

        config = Unison.plugin.getConfig();
        try {
            config.load(Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile());
        } catch (IOException | InvalidConfigurationException e) {

        }
        loadValues();
        Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile().delete();
        Unison.plugin.saveResource("config.yml", false);
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
        if (config.contains("bedrock-offhand-items-only", true)) {
            bedrockOffhandItemsOnly = config.getBoolean("bedrock-offhand-items-only");
        }
        if (config.contains("bedrock-consumable-effects", true)) {
            bedrockConsumableEffects = config.getBoolean("bedrock-consumable-effects");
        }
        if (config.contains("allow-block-only-when-sneaking", true)) {
            blockWhenSneaking = config.getBoolean("allow-block-only-when-sneaking");
        }
    }

    public static void saveValues() {
        config.set("player-range-type", playerRangeType.name().toLowerCase());
        config.set("enable-arms-on-armor-stands", enableArmsOnArmorStands);
        config.set("disable-offhand-keybind", disableOffhandKeybind);
        config.set("disable-number-key-inventory-hotkey", disableNumberKeyInventoryHotkey);
        config.set("bedrock-offhand-items-only", bedrockOffhandItemsOnly);
        config.set("bedrock-consumable-effects", bedrockConsumableEffects);
        config.set("allow-block-only-when-sneaking", blockWhenSneaking);
        try {
            config.save(Unison.plugin.getDataFolder().toPath().resolve("config.yml").toFile());
        } catch (IOException e) {
            //
        }
    }
}
