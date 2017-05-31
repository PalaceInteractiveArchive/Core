package network.palace.core.player;

/**
 * The interface C player resource pack manager.
 */
public interface CPlayerResourcePackManager {
    /**
     * Sends resource pack from url.
     *
     * @param url the url of the resource pack to send
     */
    void send(String url);

    /**
     * Sends resource pack from url.
     *
     * @param url  the url of the resource pack to send
     * @param hash the hash the of the resource pack
     */
    void send(String url, String hash);
}
