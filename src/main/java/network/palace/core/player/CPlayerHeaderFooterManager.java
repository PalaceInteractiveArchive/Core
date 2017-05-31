package network.palace.core.player;

/**
 * The interface C player header footer manager.
 */
public interface CPlayerHeaderFooterManager {
    /**
     * Sets footer.
     *
     * @param footer the new footer text
     */
    void setFooter(String footer);

    /**
     * Sets header.
     *
     * @param header the new header text
     */
    void setHeader(String header);

    /**
     * Sets header and footer in one method
     *
     * @param header the new header text
     * @param footer the new footer text
     */
    void setHeaderFooter(String header, String footer);

    /**
     * Hide header and footer.
     */
    void hide();

    /**
     * Update header and footer.
     */
    void update();
}
