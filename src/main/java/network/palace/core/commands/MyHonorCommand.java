package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

/**
 * @author Marc
 * @since 6/18/17
 */
@CommandMeta(description = "Get your current honor count and level")
public class MyHonorCommand extends CoreCommand {
    private DecimalFormat format = new DecimalFormat("#,###");

    public MyHonorCommand() {
        super("myhonor");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        int honor = player.getHonor();
        int level = Core.getHonorManager().getLevel(honor).getLevel();
        HonorMapping nextLevel = Core.getHonorManager().getNextLevel(honor);
        float progress = Core.getHonorManager().progressToNextLevel(honor);
        player.sendMessage(ChatColor.GREEN + "You are Level " + format(level) + " with " + format(honor) +
                " Honor.\nYou are " + (nextLevel.getHonor() - honor) + " Honor (" + (int) (progress * 100.0f) +
                "%) away from Level " + nextLevel.getLevel());
    }

    private String format(int i) {
        return format.format(i);
    }
}
