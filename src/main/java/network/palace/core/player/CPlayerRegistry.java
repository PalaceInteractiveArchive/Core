package network.palace.core.player;

public interface CPlayerRegistry {

    /**
     * Get an entry's values by its key
     *
     * @param key the key
     * @return the entry's value
     */
    Object getEntry(String key);

    /**
     * Check if a key exists in the registry
     *
     * @param key the key
     * @return true if key is in registry
     */
    boolean hasEntry(String key);

    /**
     * Add an entry to the registry
     *
     * @param key the key
     * @param o   the object
     * @implNote If an entry already exists with the provided key, the existing entry will be overwritten
     */
    void addEntry(String key, Object o);

    /**
     * Remove an entry by its key
     *
     * @param key the key
     * @return the object mapped to the key if it existed, null if not
     */
    Object removeEntry(String key);

    /**
     * Clear the registry
     */
    void clearRegistry();
}
