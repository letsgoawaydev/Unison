package dev.letsgoaway.unison;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.letsgoaway.unison.listeners.packet.send.PlayPacketSendListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class Unison extends JavaPlugin {
    public static Unison plugin;

    @Override
    public void onEnable() {
        plugin = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false);
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PlayPacketSendListener(),
                PacketListenerPriority.LOW);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::update, 0L, 0L);
        Config.loadConfig();
        getCommand("unison").setExecutor(new UnisonCommand());
        getCommand("unison").setTabCompleter(new UnisonCommand());
        getCommand("coords").setExecutor(new CoordsCommand());
        getCommand("days").setExecutor(new DaysCommand());
    }
    @Override
    public void onLoad() {
    }

    public void updateReach(Player player) {
        double reach = 0D;
        switch (Config.playerRangeType) {
            case JAVA -> {
                break;
            }
            case POCKET -> {
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    reach = 6.0D;
                } else if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    reach = 12.0D;
                }
                break;
            }
            case BEDROCK -> {
                reach = 5.0D;
                break;
            }
        }
        if (reach != 0D) {
            player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(reach);
        }
    }

    ArrayList<UUID> uuidsToRemove = new ArrayList<>();
    public static int dayCount = 0;

    public void update() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                dayCount = (int) Math.floor(world.getFullTime() / 24000.0F);
                break;
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateReach(player);
        }

        for (UnisonPlayer unisonPlayer : EventListener.players.values()) {
            if (Bukkit.getPlayer(unisonPlayer.uuid) != null) {
                unisonPlayer.tick();
            } else {
                uuidsToRemove.add(unisonPlayer.uuid);
            }
        }
        for (UUID uuid : uuidsToRemove) {
            EventListener.players.remove(uuid);
        }
        uuidsToRemove.clear();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
