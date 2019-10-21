package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@CommandMeta(description = "Refresh permissions for all ranks")
public class RefreshCommand extends CoreCommand {

    public RefreshCommand() {
        super("refresh");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    protected void handle(CommandSender sender, String[] args) throws CommandException {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            sender.sendMessage(ChatColor.YELLOW + "Refreshing permissions...");
            Core.getPermissionManager().refresh();
            sender.sendMessage(ChatColor.YELLOW + "Permissions refreshed.");
        });
    }
}
