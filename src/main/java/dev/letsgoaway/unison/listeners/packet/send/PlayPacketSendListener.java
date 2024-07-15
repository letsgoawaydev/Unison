package dev.letsgoaway.unison.listeners.packet.send;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.score.ScoreFormat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import dev.letsgoaway.unison.UnisonPlayer;
import net.kyori.adventure.text.Component;

public class PlayPacketSendListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent ev) {
        if (ev.getPacketType().equals(PacketType.Play.Server.UPDATE_SCORE)) {
            WrapperPlayServerUpdateScore score = new WrapperPlayServerUpdateScore(ev);
            WrapperPlayServerUpdateScore.Action action = score.getAction();
            if (action != null && action.equals(WrapperPlayServerUpdateScore.Action.REMOVE_ITEM)) {
                return;
            }
            if (score.getObjectiveName().contains("unison-") && score.getEntityName().equals("unison:days")) {
                score.setScoreFormat(ScoreFormat.fixedScore(Component.text(UnisonPlayer.getDaysText())));
                score.setEntityName("");
            }
            ev.markForReEncode(true);
        }
    }
}
