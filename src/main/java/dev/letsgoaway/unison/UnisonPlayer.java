package dev.letsgoaway.unison;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.UUID;

public class UnisonPlayer {
    public Player player;
    public UUID uuid;
    public Objective objective;
    public Scoreboard scoreboard;
    public Score secondbar;

    public UnisonPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        createSB();
    }

    public void tick() {
        updateBedrockStyleDisplays();
    }

    public void updateBedrockStyleDisplays() {
        if (Config.showCoordinates && Config.showDaysPlayed) {
            setTopSBBar("Position: " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
            setSecondBar("Days played:", Unison.dayCount);
            return;
        }
        if (Config.showCoordinates && !Config.showDaysPlayed) {
            setTopSBBar("Position: " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
            setSecondBar("", 0);
            return;
        }
        if (!Config.showCoordinates && Config.showDaysPlayed) {
            setTopSBBar("Days played: " + Unison.dayCount);
            setSecondBar("", 0);
            return;
        }
        hideSB();
    }

    public void createSB() {
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = scoreboard.registerNewObjective("unison-" + player.getUniqueId(), Criteria.DUMMY, " ");
    }

    public boolean scoreboardVisible = false;

    public void showSB() {
        scoreboardVisible = true;
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void hideSB() {
        if (scoreboardVisible) {
            scoreboardVisible = false;
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(scoreboard);
        }
    }

    public void setTopSBBar(String s) {
        if (s.isEmpty()) {
            hideSB();
            return;
        } else if (!scoreboardVisible) {
            showSB();
        }
        objective.setDisplayName(s);
    }


    public void setSecondBar(String s, int amount) {
        if (s.isEmpty()) {
            scoreboard.resetScores("Days played:");
            return;
        }
        secondbar = objective.getScore(s);
        secondbar.setScore(amount);
    }
}
