package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 5/22/2017
 */
@AllArgsConstructor
public class StatisticType {
    @Getter private String type;

    /**
     * Statistics shared between all games
     */
    public enum Global {
        TIME_PLAYED, WINS, LOSSES
    }

    /**
     * Statistics about Outpost
     */
    public class Outpost {
        public final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public final StatisticType DEATHS = new StatisticType("deaths");
        public final StatisticType CONQUERED_TOWERS = new StatisticType("conquered_towers");
        public final StatisticType SECONDS_ON_TOWER = new StatisticType("seconds_on_tower");
    }

    /**
     * Statistics about Spleef
     */
    public class Spleef {
        public final StatisticType DEATHS = new StatisticType("deaths");
    }

    /**
     * Statistics about CowTipping
     */
    public class CowTipping {
        public final StatisticType COWS_TIPPED = new StatisticType("cows_tipped");
        public final StatisticType COWS_STOLEN = new StatisticType("cows_stolen");
    }

    /**
     * Statistics about OneShot
     */
    public class OneShot {
        public final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public final StatisticType SHOTS_MISSED = new StatisticType("shots_missed");
    }

    /**
     * Statistics about FreezeTag
     */
    public class FreezeTag {
        public final StatisticType FROZEN_PLAYERS = new StatisticType("frozen_players");
        public final StatisticType UNFROZEN_PLAYERS = new StatisticType("unfrozen_players");
    }
}
