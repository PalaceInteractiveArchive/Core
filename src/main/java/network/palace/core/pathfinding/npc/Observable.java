package network.palace.core.pathfinding.npc;

import com.google.common.collect.ImmutableSet;

public interface Observable<T> {
    void registerObservable(T observer);
    void unregisterObservable(T observer);
    ImmutableSet<T> getObservers();
}
