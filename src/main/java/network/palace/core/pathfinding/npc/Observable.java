package network.palace.core.pathfinding.npc;

import com.google.common.collect.ImmutableSet;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public interface Observable<T> {
    void registerObservable(T observer);
    void unregisterObservable(T observer);
    ImmutableSet<T> getObservers();
}
