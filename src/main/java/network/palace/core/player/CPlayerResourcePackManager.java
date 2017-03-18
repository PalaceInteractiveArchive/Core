package network.palace.core.player;

/**
 * The interface C player resource pack manager.
 */
public interface CPlayerResourcePackManager {
    /**
     * Sends resource pack from url.
     *
     * @param url the url
     */
    void send(String url);

    /**
     * Sends resource pack from url.
     *
     * @param url  the url
     * @param hash the hash
     */
    void send(String url, String hash);
}
