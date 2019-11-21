package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.dashboard.packets.dashboard.PacketRankChange;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.SponsorTier;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandMeta(description = "Player commands")
public class PlayerCommand extends CoreCommand {

    public PlayerCommand() {
        super("player");
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
        if (args.length < 2) {
            helpMenu(sender);
            return;
        }
        UUID uuid;
        String name;
        Rank rank;
        SponsorTier tier;
        CPlayer player = Core.getPlayerManager().getPlayer(args[0]);
        if (player == null) {
            uuid = Core.getMongoHandler().usernameToUUID(args[0]);
            name = args[0];
            rank = Core.getMongoHandler().getRank(uuid);
            tier = Core.getMongoHandler().getSponsorTier(uuid);
        } else {
            uuid = player.getUniqueId();
            name = player.getName();
            rank = player.getRank();
            tier = player.getSponsorTier();
        }
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "get": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                Map<String, Boolean> map = Core.getPermissionManager().getPermissions(rank);
                String node = args[2];
                Boolean val = map.get(node);
                if (val == null) {
                    sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " doesn't set a value for " + ChatColor.AQUA + node);
                    return;
                }
                if (val) {
                    sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getFormattedName() + ChatColor.GREEN + " sets true for " + ChatColor.AQUA + node);
                } else {
                    sender.sendMessage(ChatColor.RED + "Rank " + rank.getFormattedName() + ChatColor.RED + " sets false for " + ChatColor.AQUA + node);
                }
                return;
            }
            case "rank": {
                sender.sendMessage(ChatColor.GREEN + name + " has rank " + rank.getFormattedName());
                return;
            }
            case "setrank": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                Rank next = Rank.fromString(args[2]);
                if (next == null) {
                    sender.sendMessage(ChatColor.RED + args[2] + " isn't a rank!");
                    return;
                }
                Core.getMongoHandler().setRank(uuid, next);
                if (player != null) {
                    player.setRank(next);
                    for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                        Core.getPlayerManager().displayRank(player);
                    }
                }
                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                PacketRankChange packet = new PacketRankChange(uuid, next, tier, source);
                Core.getDashboardConnection().send(packet);
                sender.sendMessage(ChatColor.GREEN + name + " is now rank " + next.getFormattedName());
                return;
            }
            case "setsponsor": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                SponsorTier next = SponsorTier.fromString(args[2]);
                if (next == null) {
                    sender.sendMessage(ChatColor.RED + args[2] + " isn't a valid sponsor tier!");
                    return;
                }
                Core.getMongoHandler().setSponsorTier(uuid, next);
                if (player != null) {
                    player.setSponsorTier(next);
                    for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                        Core.getPlayerManager().displayRank(player);
                    }
                }
                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                PacketRankChange packet = new PacketRankChange(uuid, rank, next, source);
                Core.getDashboardConnection().send(packet);
                sender.sendMessage(ChatColor.GREEN + name + " is now sponsor tier " + next.getChatTag(false));
                return;
            }
        }
        helpMenu(sender);
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] get [permission] " + ChatColor.AQUA + "- Get the value of a permission for a player");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] rank " + ChatColor.AQUA + "- Get the player's rank.");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] setrank [rank] " + ChatColor.AQUA + "- Set a player's rank");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] setsponsor [tier] " + ChatColor.AQUA + "- Set a player's sponsor tier");
    }
}
