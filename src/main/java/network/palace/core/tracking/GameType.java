package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 5/22/2017
 */
@AllArgsConstructor
public enum GameType {
    OUTPOST(1), SPLEEF(2), PIXIE_DUST_SHOOTOUT(3), ONESHOT(4), FREEZETAG(5), DEATHRUN(6);
    @Getter int id;
}
