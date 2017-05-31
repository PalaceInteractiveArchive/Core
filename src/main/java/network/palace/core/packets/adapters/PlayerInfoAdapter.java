package network.palace.core.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;

import java.util.List;

/**
 * @author Innectic
 * @since 5/28/2017
 */
public class PlayerInfoAdapter extends PacketAdapter {

    public PlayerInfoAdapter() {
        super(Core.getInstance(), PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event == null) return;
        if (event.getPacket() == null) return;
        if (event.getPacket().getSpecificModifier(List.class) == null) return;
        if (event.getPacket().getSpecificModifier(List.class).read(0) == null) return;
        List playerInfo = event.getPacket().getSpecificModifier(List.class).read(0);
        for (Object infoDataObj : playerInfo) {
            if (infoDataObj instanceof PlayerInfoData) {
                PlayerInfoData infoData = (PlayerInfoData) infoDataObj;
                CPlayer player = Core.getPlayerManager().getPlayer(infoData.getProfile().getName());
                if (player == null) return;
                player.setPing(infoData.getLatency());
            }
        }
    }
}
