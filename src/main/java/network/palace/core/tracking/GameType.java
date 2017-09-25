package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 5/22/2017
 */
@AllArgsConstructor
public enum GameType {
    OUTPOST(1, "Outpost", "outpost"), SPLEEF(2, "Spleef", "spleef"), PIXIE_DUST_SHOOTOUT(3, "Pixie Dust Shootout",
            "pixiedustshootout"), ONESHOT(4, "One Shot", "oneshot"), FREEZETAG(5, "Freeze Tag", "freezetag");
    @Getter int id;
    @Getter String name;
    @Getter String dbName;
}
