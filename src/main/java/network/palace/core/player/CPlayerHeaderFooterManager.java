package network.palace.core.player;

@SuppressWarnings("unused")
public interface CPlayerHeaderFooterManager {

    void setFooter(String footer);
    void setHeader(String header);
    void hide();
    void update();

}
