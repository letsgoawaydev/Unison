package dev.letsgoaway.unison;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.UUID;

public class UnisonPlayer {
    public Player player;
    public UUID uuid;
    public Objective objective;
    public Scoreboard scoreboard;
    public Score secondbar;
    public boolean showCoordinates = true;

    public boolean showDaysPlayed = true;

    public UnisonPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        if (hasData("coordinates"))
            setShowCoordinates(Config.allowTogglingCoords ? getData("coordinates", PersistentDataType.BOOLEAN) : Config.showCoordinates, true);
        else
            setShowCoordinates(Config.showCoordinates, true);

        if (hasData("daysplayed"))
            setShowDaysPlayed(Config.allowTogglingDays ? getData("daysplayed", PersistentDataType.BOOLEAN) : Config.showDaysPlayed, true);
        else
            setShowDaysPlayed(Config.showDaysPlayed, true);

        createSB();
    }

    public static UnisonPlayer get(Player player) {
        return EventListener.players.get(player.getUniqueId());
    }

    public void tick() {
        updateBedrockStyleDisplays();
    }

    public void setShowCoordinates(boolean showCoordinates) {
        setShowCoordinates(showCoordinates, false);
    }

    public void setShowCoordinates(boolean showCoordinates, boolean force) {
        if (Config.allowTogglingCoords || force) {
            this.showCoordinates = showCoordinates;
            setData("coordinates", PersistentDataType.BOOLEAN, showCoordinates);
        } else {
            player.sendMessage("Toggling the Bedrock Coordinates UI is disabled on this server!");
        }
    }

    public void setShowDaysPlayed(boolean daysPlayed) {
        setShowDaysPlayed(daysPlayed, false);
    }

    public void setShowDaysPlayed(boolean showDaysPlayed, boolean force) {
        if (Config.allowTogglingDays || force) {
            this.showDaysPlayed = showDaysPlayed;
            setData("daysplayed", PersistentDataType.BOOLEAN, showDaysPlayed);
        } else {
            player.sendMessage("Toggling the Bedrock Days Played UI is disabled on this server!");
        }
    }

    public void updateBedrockStyleDisplays() {
        boolean coordsCanDisplay = Config.allowTogglingCoords ? this.showCoordinates : Config.showCoordinates;
        boolean daysCanDisplay = Config.allowTogglingDays ? this.showDaysPlayed : Config.showDaysPlayed;
        if (coordsCanDisplay && daysCanDisplay) {
            // Handled in PlayPacketSendListener
            setTopSBBar(getPositionText());
            // We replace this with nothing because it gets formatted in PlayPacketSendListener
            setSecondBar("unison:days", 0);
            return;
        }
        if (coordsCanDisplay && !daysCanDisplay) {
            setTopSBBar(getPositionText());
            setSecondBar("", 0);
            scoreboard.resetScores("");
            return;
        }
        if (!coordsCanDisplay && daysCanDisplay) {
            setTopSBBar(getDaysText());
            setSecondBar("", 0);
            scoreboard.resetScores("");
            return;
        }
        hideSB();
    }

    public PersistentDataContainer playerSaveData() {
        return player.getPersistentDataContainer();
    }

    public boolean hasData(String key) {
        try {
            return playerSaveData().has(NamespacedKey.fromString(key, Unison.plugin));
        } catch (Exception ignored) {
            return false;
        }
    }

    public <P, C> void setData(String key, PersistentDataType<P, C> type, C value) {
        playerSaveData().set(NamespacedKey.fromString(key, Unison.plugin), type, value);
    }


    public <P, C> C getData(String key, PersistentDataType<P, C> type) {
        return playerSaveData().get(NamespacedKey.fromString(key, Unison.plugin), type);
    }

    public String getPositionText() {
        return "Position: " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
    }

    public static String getDaysText() {
        return "Days played: " + Unison.dayCount;
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

    String lastSecondBar = "";

    public void setSecondBar(String s, int amount) {
        scoreboard.resetScores(lastSecondBar);
        if (s.isEmpty()) {
            return;
        }
        secondbar = objective.getScore(s);
        secondbar.setScore(amount);
        lastSecondBar = s;
    }
}
