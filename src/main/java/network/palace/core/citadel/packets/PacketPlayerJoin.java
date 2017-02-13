package network.palace.core.citadel.packets;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class PacketPlayerJoin extends PacketBase {

    private String playerName;
    private String playerUUID;
    private String time;

    public PacketPlayerJoin(String server, String playerName, String playerUUID, String time) {
        super("playerJoin", server);

        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.time = time;
    }

    @Override
    public String toString() {
        return "PacketPlayerJoin [" +
                "type=" + getType() +
                ", server=" + getServer() +
                ", playerName=" + playerName +
                ", playerUUID=" + playerUUID +
                ", time=" + time +
                "]";
    }
}
