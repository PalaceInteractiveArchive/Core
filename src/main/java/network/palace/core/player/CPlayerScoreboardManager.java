package network.palace.core.player;

@SuppressWarnings("unused")
public interface CPlayerScoreboardManager {

    CPlayerScoreboardManager set(int id, String text);
    CPlayerScoreboardManager setBlank(int id);
    CPlayerScoreboardManager remove(int id);
    CPlayerScoreboardManager title(String title);
}