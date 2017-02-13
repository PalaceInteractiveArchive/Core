package network.palace.core.citadel.packets;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class PacketPlayerChat extends PacketBase {

    private String chatMessage;
    private int rank;
    private String playerName;
    private String playerUUID;

    public PacketPlayerChat(String server, String playerName, String playerUUID, String chatMessage, int rank) {
        super("chat", server);

        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.chatMessage = chatMessage;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "PacketPlayerChat [" +
                "type=" + getType() +
                ", server=" + getServer() +
                ", chatMessage=" + chatMessage +
                ", rank=" + rank +
                ", playerName=" + playerName +
                ", playerUUID=" + playerUUID +
                "]";
    }
}
