package network.palace.core.player.impl;

import lombok.RequiredArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerRegistry;

import java.util.HashMap;

@RequiredArgsConstructor
public class CorePlayerRegistry implements CPlayerRegistry {
    private final CPlayer player;
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    public Object getEntry(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        return map.get(key);
    }

    @Override
    public boolean hasEntry(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        return map.containsKey(key);
    }

    @Override
    public void addEntry(String key, Object o) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        if (o == null) throw new IllegalArgumentException("entry object cannot be null!");
        map.put(key, o);
    }

    @Override
    public Object removeEntry(String key) {
        return map.remove(key);
    }

    @Override
    public void clearRegistry() {
        map.clear();
    }
}
