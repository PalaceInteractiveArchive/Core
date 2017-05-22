package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;

/**
 * @author Innectic
 * @since 5/22/2017
 */
@AllArgsConstructor
public class GameStatisticChangeEvent extends CoreEvent {
    /**
     * The player who's stats were changed
     */
    @Getter private CPlayer player;

    /**
     * The type of game
     */
    @Getter private GameType gameType;

    /**
     * The type of statistic
     */
    @Getter private StatisticType statisticType;

    /**
     * The amount of the statistic
     */
    @Getter private int amount;
}
