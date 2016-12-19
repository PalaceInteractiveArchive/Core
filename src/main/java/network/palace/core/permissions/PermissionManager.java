package network.palace.core.permissions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marc on 12/19/16.
 */
public class PermissionManager {
    public HashMap<UUID, PermissionAttachment> attachments = new HashMap<>();
    private HashMap<Rank, HashMap<String, Boolean>> permissions = new HashMap<>();

    public PermissionManager() {
        initialize();
    }

    public void initialize() {
        permissions.clear();
        Rank[] ranks = Rank.values();
        Collection<CPlayer> players = Core.getPlayerManager().getOnlinePlayers();
        boolean empty = players.isEmpty();
        Rank previous = null;
        for (int i = ranks.length - 1; i >= 0; i--) {
            Rank r = ranks[i];
            HashMap<String, Boolean> perms = Core.getSqlUtil().getPermissions(r);
            if (previous != null) {
                for (Map.Entry<String, Boolean> perm : getPermissions(previous).entrySet()) {
                    if (perms.containsKey(perm.getKey())) {
                        if (!perms.get(perm.getKey()).equals(perm.getValue())) {
                            continue;
                        }
                    }
                    perms.put(perm.getKey(), perm.getValue());
                }
            }
            permissions.put(r, perms);
            if (!empty) {
                for (CPlayer p : players) {
                    if (p.getRank().equals(r)) {
                        setPermissions(p.getBukkitPlayer(), perms);
                    }
                }
            }
            previous = r;
        }
    }

    public void login(CPlayer player) {
        setPermissions(player.getBukkitPlayer(), getPermissions(player.getRank()));
    }

    private void setPermissions(Player player, HashMap<String, Boolean> perms) {
        PermissionAttachment attachment;
        if (attachments.containsKey(player.getUniqueId())) {
            attachment = attachments.get(player.getUniqueId());
        } else {
            attachment = player.addAttachment(Core.getInstance());
        }
        for (Map.Entry<String, Boolean> entry : attachment.getPermissions().entrySet()) {
            attachment.unsetPermission(entry.getKey());
        }
        attachment.remove();
        for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
            attachment.setPermission(entry.getKey(), entry.getValue());
        }
    }

    public HashMap<String, Boolean> getPermissions(Rank rank) {
        HashMap<String, Boolean> map = permissions.get(rank);
        return map == null ? new HashMap<>() : map;
    }

    public void refresh(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            sender.sendMessage(ChatColor.YELLOW + "Refreshing permissions...");
            initialize();
            sender.sendMessage(ChatColor.YELLOW + "Permissions refreshed!");
        });
    }

    public void setPermission(Rank rank, String node, boolean value) {
        HashMap<String, Boolean> map = new HashMap<>(permissions.get(rank));
        map.put(node, value);
        permissions.put(rank, map);
    }

    public void unsetPermission(Rank rank, String node) {
        HashMap<String, Boolean> map = new HashMap<>(permissions.get(rank));
        map.remove(node);
        permissions.put(rank, map);
    }
}
