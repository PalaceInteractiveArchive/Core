package network.palace.core.achievements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 6/26/16
 */
@AllArgsConstructor
public class CoreAchievement {
    @Getter private int id;
    @Getter @Setter private String displayName;
    @Getter @Setter private String description;
}