package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Award an achievement to a player")
@CommandPermission(rank = Rank.MOD)
public class AchievementCommand extends CoreCommand {

    public AchievementCommand() {
        super("ach");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/ach [ID] [Username]");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "/ach [ID] [Username]");
            return;
        }
        Integer id = Integer.parseInt(args[0]);
        CoreAchievement ach = Core.getAchievementManager().getAchievement(id);
        if (ach == null) {
            sender.sendMessage(ChatColor.RED + "There is no achievement with ID " + id + "!");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        player.giveAchievement(id);
    }
}
