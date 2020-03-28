package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerAchievementManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.List;

@AllArgsConstructor
public class CorePlayerAchievementManager implements CPlayerAchievementManager {

    private final CPlayer player;
    private List<Integer> achievements;

    @Override
    public List<Integer> getAchievements() {
        return achievements;
    }

    @Override
    public boolean hasAchievement(int i) {
        return achievements.contains(i);
    }

    @Override
    public void giveAchievement(int i) {
        if (hasAchievement(i)) {
            return;
        }
        achievements.add(i);
        CoreAchievement ach = Core.getAchievementManager().getAchievement(i);
        if (ach == null) {
            return;
        }
        player.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD + "Achievement" +
                ChatColor.GREEN + "--------------\n" + ChatColor.AQUA + ach.getDisplayName() + "\n" + ChatColor.GRAY +
                "" + ChatColor.ITALIC + ach.getDescription() + ChatColor.GREEN + "\n----------------------------------------");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
//        Core.runTaskAsynchronously(() -> Core.getSqlUtil().addAchievement(player, i));
        Core.runTaskAsynchronously(Core.getInstance(), () -> Core.getMongoHandler().addAchievement(player.getUniqueId(), i));
        Core.getMongoHandler().changeAmount(player.getUniqueId(), 5, "Achievement ID " + i, CurrencyType.TOKENS, false);
        //TODO Make honor
    }
}
