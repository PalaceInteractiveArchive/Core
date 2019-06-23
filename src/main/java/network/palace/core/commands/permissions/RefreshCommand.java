package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Refresh permissions for all ranks")
public class RefreshCommand extends CoreCommand {

    public RefreshCommand() {
        super("refresh");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            sender.sendMessage(ChatColor.YELLOW + "Refreshing permissions...");
            Core.getPermissionManager().refresh();
            sender.sendMessage(ChatColor.YELLOW + "Permissions refreshed.");
        });
    }
}
