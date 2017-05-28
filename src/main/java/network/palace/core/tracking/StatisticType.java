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
    public static class Outpost {
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public static final StatisticType DEATHS = new StatisticType("deaths");
        public static final StatisticType CONQUERED_TOWERS = new StatisticType("conquered_towers");
        public static final StatisticType SECONDS_ON_TOWER = new StatisticType("seconds_on_tower");
    }

    /**
     * Statistics about Spleef
     */
    public static class Spleef {
        public static final StatisticType DEATHS = new StatisticType("deaths");
    }

    /**
     * Statistics about CowTipping
     */
    public static class CowTipping {
        public static final StatisticType COWS_TIPPED = new StatisticType("cows_tipped");
        public static final StatisticType COWS_STOLEN = new StatisticType("cows_stolen");
        public static final StatisticType TOTAL_POINTS = new StatisticType("cowtipping_total_points");
    }

    /**
     * Statistics about OneShot
     */
    public static class OneShot {
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public static final StatisticType SHOTS_MISSED = new StatisticType("shots_missed");
    }

    /**
     * Statistics about FreezeTag
     */
    public static class FreezeTag {
        public static final StatisticType FROZEN_PLAYERS = new StatisticType("frozen_players");
        public static final StatisticType UNFROZEN_PLAYERS = new StatisticType("unfrozen_players");
    }
}
