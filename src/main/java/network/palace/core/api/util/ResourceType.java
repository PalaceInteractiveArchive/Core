package network.palace.core.api.util;

public enum ResourceType {

    ACHIEVEMENTS("achievements"),
    CHALLENGES("challenges"),
    QUEST("quests");

    /**
     * Path to resource
     */
    private String path;

    ResourceType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
