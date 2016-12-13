package com.palacemc.core.commands;

import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CommandPermission;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.dashboard.packets.dashboard.PacketEmptyServer;
import com.palacemc.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 12/9/16.
 */
@CommandMeta(description = "Safely stop the server.")
@CommandPermission(rank = Rank.WIZARD)
public class SafestopCommand extends CoreCommand {

    private Core instance = Core.getPlugin(Core.class);

    public SafestopCommand() {
        super("safestop");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Shutting the server down...");

        for (World world : Bukkit.getWorlds()) {
            world.save();
        }

        PacketEmptyServer packet = new PacketEmptyServer(instance.getInstanceName());
        instance.getDashboardConnection().send(packet);

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                instance.getDashboardConnection().stop();
                Bukkit.shutdown();
            }
        }, 0L, 40L);
    }
}
