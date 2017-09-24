package network.palace.core.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.Rank;

import java.util.UUID;

/**
 * @author Marc
 * @since 9/24/17
 */
@AllArgsConstructor
public class JoinReport {
    @Getter private final UUID uuid;
    @Getter private final Rank rank;
}
