package network.palace.core.player;

@SuppressWarnings("unused")
public interface CPlayerResourcePackManager {

    void send(String url);
    void send(String url, String hash);

}
