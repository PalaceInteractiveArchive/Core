package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.dashboard.packets.dashboard.PacketEmptyServer;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * Server shutdown command.
 */
@CommandMeta(description = "Safely stop the server.")
@CommandPermission(rank = Rank.DEVELOPER)
public class ShutdownCommand extends CoreCommand {
    private int taskID = 0;

    /**
     * Instantiates a new Safestop command.
     */
    public ShutdownCommand() {
        super("shutdown");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/shutdown [delay]");
            sender.sendMessage(ChatColor.RED + "/shutdown cancel " + ChatColor.AQUA + "- Cancel existing shutdown");
            return;
        }
        if (args[0].equalsIgnoreCase("cancel")) {
            if (taskID == 0) {
                sender.sendMessage(ChatColor.RED + "No shutdown is pending!");
                return;
            }
            Core.cancelTask(taskID);
            taskID = 0;
            sender.sendMessage(ChatColor.GREEN + "Shutdown cancelled!");
            return;
        }
        if (taskID != 0) {
            sender.sendMessage(ChatColor.RED + "A shutdown is already pending! Run '/shutdown cancel' to cancel it.");
            return;
        }
        final int delay;
        try {
            delay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "/shutdown [delay]");
            return;
        }
        sender.sendMessage(ChatColor.RED + "Shutting the server down in " + delay + " seconds...");
        taskID = Core.runTaskTimer(new Runnable() {
            int i = delay;

            @Override
            public void run() {
                if (i > 0) {
                    i--;
                    message(i);
                    return;
                }
                Core.setStarting(true);
                Bukkit.getWorlds().forEach(World::save);
                Core.getDashboardConnection().send(new PacketEmptyServer(Core.getInstanceName()));
                Core.runTaskTimer(() -> {
                    if (Bukkit.getOnlinePlayers().size() <= 0) {
                        Core.getDashboardConnection().stop();
                        Bukkit.shutdown();
                    }
                }, 0L, 40L);
            }
        }, 0L, 20L);
    }

    public void message(int seconds) {
        if (seconds <= 0) {
            return;
        }
        String time = "";
        if (seconds > 60 && seconds % 60 == 0) {
            time = seconds / 60 + " minutes";
        } else if (seconds == 60) {
            time = "1 minute";
        } else if (seconds == 30) {
            time = "30 seconds";
        } else {
            return;
        }
        String msg = ChatColor.LIGHT_PURPLE + "[Server] " + ChatColor.GREEN + "This server (" + Core.getInstanceName() + ") will restart in " + time;
        for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }
}
