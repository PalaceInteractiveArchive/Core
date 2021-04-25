package network.palace.core.commands.permissions;

import lombok.val;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.BotRankChangePacket;
import network.palace.core.messagequeue.packets.RankChangePacket;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        List<String> tags = new ArrayList<>();
        CPlayer player = Core.getPlayerManager().getPlayer(args[0]);
        if (player == null) {
            uuid = Core.getMongoHandler().usernameToUUID(args[0]);
            name = args[0];
            rank = Core.getMongoHandler().getRank(uuid);
            Core.getMongoHandler().getRankTags(uuid).forEach(t -> tags.add(t.getDBName()));
        } else {
            uuid = player.getUniqueId();
            name = player.getName();
            rank = player.getRank();
            player.getTags().forEach(t -> tags.add(t.getDBName()));
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
            case "tags": {
                if (tags.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + name + " doesn't have any tags!");
                    return;
                }
                sender.sendMessage(ChatColor.GREEN + name + "'s Tags (" + tags.size() + "):");
                for (String s : tags) {
                    sender.sendMessage(ChatColor.GREEN + "- " + s);
                }
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
                if (sender instanceof Player) {
                    if (next.getRankId() > Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId()).getRank().getRankId()) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to set a player to " + next.getFormattedName() + "!");
                        return;
                    }
                }
                Core.getMongoHandler().setRank(uuid, next);
                if (player != null) {
                    player.setRank(next);
                    for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                        Core.getPlayerManager().displayRank(player);
                    }
                }
                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                try {
                    Core.getMessageHandler().sendMessage(new RankChangePacket(uuid, next, tags, source), Core.getMessageHandler().ALL_PROXIES);
                } catch (IOException e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error communicating player rank change", e);
                }
                sender.sendMessage(ChatColor.GREEN + name + " is now rank " + next.getFormattedName());
                try {
                    val discordId = Core.getMongoHandler().getUserDiscordId(player.getUuid());
                    if (!discordId.equals("")) {
                        String userTags = player.getTags()
                                .stream()
                                .map(a -> a.getDBName())
                                .collect(Collectors.joining(","));
                        Core.getMessageHandler().sendMessage(new BotRankChangePacket(next.getDBName(), name, discordId, userTags), Core.getMessageHandler().BOT);
                    }
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return;
            }
            case "addtag": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                if (tags.size() >= 3) {
                    sender.sendMessage(ChatColor.RED + "Players can't have more than three tags!");
                    return;
                }
                RankTag tag = RankTag.fromString(args[2]);
                if (tag == null) {
                    sender.sendMessage(ChatColor.RED + args[2] + " isn't a valid tag!");
                    return;
                }
                if (tags.contains(tag.getDBName())) {
                    sender.sendMessage(ChatColor.RED + "That player already has that tag!");
                    return;
                }
                Core.getMongoHandler().addRankTag(uuid, tag);
                tags.add(tag.getDBName());
                if (player != null) {
                    player.addTag(tag);
                    for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                        Core.getPlayerManager().displayRank(player);
                    }
                }
                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                try {
                    Core.getMessageHandler().sendMessage(new RankChangePacket(uuid, rank, tags, source), Core.getMessageHandler().ALL_PROXIES);
                } catch (IOException e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error communicating player rank change", e);
                }
                sender.sendMessage(ChatColor.GREEN + name + " now has the tag " + tag.getName());
                return;
            }
            case "removetag": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                RankTag tag = RankTag.fromString(args[2]);
                if (tag == null) {
                    sender.sendMessage(ChatColor.RED + args[2] + " isn't a valid tag!");
                    return;
                }
                if (!tags.contains(tag.getDBName())) {
                    sender.sendMessage(ChatColor.RED + "That player doesn't have that tag!");
                    return;
                }
                Core.getMongoHandler().removeRankTag(uuid, tag);
                tags.remove(tag.getDBName());
                if (player != null) {
                    player.removeTag(tag);
                    for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                        Core.getPlayerManager().displayRank(player);
                    }
                }
                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                try {
                    Core.getMessageHandler().sendMessage(new RankChangePacket(uuid, rank, tags, source), Core.getMessageHandler().ALL_PROXIES);
                } catch (IOException e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error communicating player rank change", e);
                }
                sender.sendMessage(ChatColor.GREEN + name + " no longer has the tag " + tag.getName());
                return;
            }
        }
        helpMenu(sender);
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] get [permission] " + ChatColor.AQUA + "- Get the value of a permission for a player");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] rank " + ChatColor.AQUA + "- Get the player's rank.");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] tags " + ChatColor.AQUA + "- Get the player's tags.");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] setrank [rank] " + ChatColor.AQUA + "- Set a player's rank");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] addtag [tag] " + ChatColor.AQUA + "- Add a rank tag to a player");
        sender.sendMessage(ChatColor.GREEN + "/perm player [player] removetag [tag] " + ChatColor.AQUA + "- Remove a rank tag from a player");
    }
}
