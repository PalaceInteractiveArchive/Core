package network.palace.core.player;

/**
 * The interface C player header footer manager.
 */
public interface CPlayerHeaderFooterManager {
    /**
     * Sets footer.
     *
     * @param footer the footer
     */
    void setFooter(String footer);

    /**
     * Sets header.
     *
     * @param header the header
     */
    void setHeader(String header);

    /**
     * Sets header and footer in one method
     *
     * @param header the header
     * @param footer the footer
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
