package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.dashboard.packets.dashboard.PacketEmptyServer;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

@CommandMeta(description = "Disable stop command for players and command blocks")
public class StopCommand extends CoreCommand {

    public StopCommand() {
        super("stop");
    }

    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "Shutting the server down...");
        Core.setStarting(true);
        Bukkit.getWorlds().forEach(World::save);
        PacketEmptyServer packet = new PacketEmptyServer(Core.getInstanceName());
        Core.getDashboardConnection().send(packet);
        Core.runTaskTimer(() -> {
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                Core.getDashboardConnection().stop();
                Bukkit.shutdown();
            }
        }, 0L, 40L);
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.RED + "Disabled");
    }

    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "Disabled");
    }
}
