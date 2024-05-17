package dev.letsgoaway.unison;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Tick {
    public static void runOnNext(Runnable task){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Unison.plugin, task);
    }

    public static BukkitTask runIn(long ticks, Runnable task){
        return Bukkit.getScheduler().runTaskLater(Unison.plugin, task, ticks);
    }

    public static Thread runAsync(Runnable task){
        return Thread.startVirtualThread(task);
    }
    public static long secondsToTicksRounded(float seconds) {
        return Math.round(secondsToTicks(seconds));
    }
    public static float secondsToTicks(float seconds) {
        return seconds * Bukkit.getServerTickManager().getTickRate();
    }
}
