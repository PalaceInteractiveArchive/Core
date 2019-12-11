package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Send message", aliases = {"tell", "t", "w", "whisper", "m"}, rank = Rank.MOD)
public class MsgCommand extends CoreCommand {

    public MsgCommand() {
        super("msg");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Player) return;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/msg [player] [message]");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(args[0]);
        if (player == null) return;
        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', msg.toString()));
    }
}
