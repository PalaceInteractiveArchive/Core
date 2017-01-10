package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.dashboard.packets.dashboard.PacketEmptyServer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * The type Safestop command.
 */
@CommandMeta(description = "Safely stop the server.")
@CommandPermission(rank = Rank.WIZARD)
public class SafestopCommand extends CoreCommand {

    /**
     * Instantiates a new Safestop command.
     */
    public SafestopCommand() {
        super("safestop");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Shutting the server down...");
        Core.setStarting(true);
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }
        PacketEmptyServer packet = new PacketEmptyServer(Core.getInstanceName());
        Core.getDashboardConnection().send(packet);
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                Core.getDashboardConnection().stop();
                Bukkit.shutdown();
            }
        }, 0L, 40L);
    }
}
