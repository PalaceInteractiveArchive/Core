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

    public static class Global {
        public static final StatisticType TIME_PLAYED = new StatisticType("time_played");
        public static final StatisticType WINS = new StatisticType("wins");
        public static final StatisticType LOSSES = new StatisticType("losses");
    }

    public static class Outpost {
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public static final StatisticType DEATHS = new StatisticType("deaths");
        public static final StatisticType CONQUERED_TOWERS = new StatisticType("conquered_towers");
        public static final StatisticType SECONDS_ON_TOWER = new StatisticType("seconds_on_tower");
    }

    public static class Spleef {
        public static final StatisticType DEATHS = new StatisticType("deaths");
        public static final StatisticType BLOCKS_REMOVED = new StatisticType("blocks_removed");
    }

    public static class OneShot {
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
        public static final StatisticType SHOTS_MISSED = new StatisticType("shots_missed");
    }

    public static class FreezeTag {
        public static final StatisticType FROZEN_PLAYERS = new StatisticType("frozen_players");
        public static final StatisticType UNFROZEN_PLAYERS = new StatisticType("unfrozen_players");
    }

    public static class PIXIE_DUST_SHOOTOUT {
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
    }
}
