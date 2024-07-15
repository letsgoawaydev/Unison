package dev.letsgoaway.unison;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DaysCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            UnisonPlayer unisonPlayer = UnisonPlayer.get(player);
            unisonPlayer.setShowDaysPlayed(!unisonPlayer.showDaysPlayed);
        }
        return true;
    }
}