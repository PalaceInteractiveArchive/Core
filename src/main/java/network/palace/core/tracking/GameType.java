package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 5/22/2017
 */
@AllArgsConstructor
public enum GameType {
    DEATHRUN(1, "Deathrun", "deathrun"), PIXIE_DUST_SHOOTOUT(2, "Pixie Dust Shootout", "pixie");
    @Getter int id;
    @Getter String name;
    @Getter String dbName;
}
