package network.palace.core.pathfinding.npc;

public enum ClickAction {

    RIGHT_CLICK,
    LEFT_CLICK;

    public static ClickAction from(String action) {
        switch (action) {
            case "INTERACT":
                return RIGHT_CLICK;
            case "ATTACK":
                return LEFT_CLICK;
        }
        return null;
    }
}
