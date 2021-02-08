package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.EmptyServerPacket;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.IOException;

@CommandMeta(description = "Disable stop command for players and command blocks")
public class StopCommand extends CoreCommand {

    private int timesTried = 0;

    public StopCommand() {
        super("stop");
    }

    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "Shutting the server down...");
        Core.setStarting(true);
        Bukkit.getWorlds().forEach(World::save);
        try {
            Core.getMessageHandler().sendMessage(new EmptyServerPacket(Core.getInstanceName()), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Core.runTaskTimer(Core.getInstance(), () -> {
            if (timesTried >= 5) {
                Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + "Server is stopping. Please rejoin in a few!"));
            }
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                Bukkit.shutdown();
            } else {
                timesTried++;
            }
        }, 40L, 40L);
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
