package dev.letsgoaway.unison;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoordsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            UnisonPlayer unisonPlayer = UnisonPlayer.get(player);
            unisonPlayer.setShowCoordinates(!unisonPlayer.showCoordinates);
        }
        return true;
    }
}