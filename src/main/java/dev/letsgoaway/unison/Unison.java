package dev.letsgoaway.unison;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Unison extends JavaPlugin {
    public static Unison plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::update, 0L, 0L);
        Config.loadConfig();
        getCommand("unison").setExecutor(new UnisonCommand());
    }

    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double reach = 0D;
            switch (Config.playerRangeType) {
                case JAVA -> {
                    break;
                }
                case POCKET -> {
                    if (player.getGameMode().equals(GameMode.SURVIVAL)){
                        reach = 6.0D;
                    }
                    else if (player.getGameMode().equals(GameMode.CREATIVE)){
                        reach = 12.0D;
                    }
                    break;
                }
                case BEDROCK -> {
                    reach = 5.0D;
                    break;
                }
            }
            if (reach != 0D){
                player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(reach);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
