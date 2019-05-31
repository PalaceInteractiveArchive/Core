package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketConfirmPlayer;
import network.palace.core.dashboard.packets.dashboard.PacketGetPack;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.*;
import network.palace.core.player.impl.CorePlayer;
import network.palace.core.player.impl.CorePlayerDefaultScoreboard;
import network.palace.core.player.impl.listeners.CorePlayerManagerListener;
import network.palace.core.player.impl.listeners.CorePlayerStaffLoginListener;
import network.palace.core.utils.ProtocolUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The type Core player manager.
 */
public class CorePlayerManager implements CPlayerManager {

    private final CorePlayerDefaultScoreboard defaultScoreboard;
    private final Map<UUID, CPlayer> onlinePlayers = new HashMap<>();

    /**
     * Instantiates a new Core player manager.
     */
    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
        Core.registerListener(new CorePlayerStaffLoginListener());
        defaultScoreboard = new CorePlayerDefaultScoreboard();
    }

    @Override
    public void playerLoggedIn(UUID uuid, String name) {
        Document joinData = Core.getMongoHandler().getJoinData(uuid, "rank", "sponsor");
        onlinePlayers.put(uuid, new CorePlayer(uuid, name,
                joinData.containsKey("rank") ? Rank.fromString(joinData.getString("rank")) : Rank.SETTLER,
                joinData.containsKey("sponsor") ? SponsorTier.fromString(joinData.getString("sponsor")) : SponsorTier.NONE,
                "en_us"));
    }

    @Override
    public void playerJoined(Player player) {
        // Get core player
        CPlayer corePlayer = getPlayer(player);
        if (corePlayer == null) return;
        corePlayer.setProtocolId(ProtocolUtil.getProtocolVersion(player));
        // Joined
        corePlayer.setStatus(PlayerStatus.JOINED);

        // Async Task
        Core.runTaskAsynchronously(() -> {
            // Cache Skin
            WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
            Optional<WrappedSignedProperty> propertyOptional = wrappedGameProfile.getProperties().get("textures").stream().findFirst();
            if (propertyOptional.isPresent()) {
                WrappedSignedProperty property = propertyOptional.get();
                corePlayer.setTextureValue(property.getValue());
                corePlayer.setTextureSignature(property.getSignature());
            }
            Core.getMongoHandler().cacheSkin(corePlayer.getUniqueId(), corePlayer.getTextureValue(), corePlayer.getTextureSignature());

            // Achievements
            List<Integer> ids = Core.getMongoHandler().getAchievements(corePlayer.getUniqueId());
            corePlayer.setAchievementManager(new CorePlayerAchievementManager(corePlayer, ids));
            Core.getCraftingMenu().update(corePlayer, 2, Core.getCraftingMenu().getAchievement(corePlayer));
            corePlayer.setHonor(Core.getMongoHandler().getHonor(corePlayer.getUniqueId()));
            corePlayer.setPreviousHonorLevel(Core.getHonorManager().getLevel(corePlayer.getHonor()).getLevel());
            corePlayer.giveAchievement(0);
            Core.getHonorManager().displayHonor(corePlayer, true);

            // Packets
            Core.getDashboardConnection().send(new PacketConfirmPlayer(corePlayer.getUniqueId(), false));
            Core.getDashboardConnection().send(new PacketGetPack(corePlayer.getUniqueId(), ""));
        });

        // Setup permissions for player
        Core.getPermissionManager().login(corePlayer);
        // Display the scoreboard
        displayRank(corePlayer);
        // Tab header and footer
        corePlayer.getHeaderFooter().setHeaderFooter(Core.getInstance().getTabHeader(), Core.getInstance().getTabFooter());
        // Show the title if we're supposed to
        if (Core.getInstance().isShowTitleOnLogin()) {
            player.sendTitle(Core.getInstance().getLoginTitle(), Core.getInstance().getLoginSubTitle(),
                    Core.getInstance().getLoginTitleFadeIn(), Core.getInstance().getLoginTitleStay(), Core.getInstance().getLoginTitleFadeOut());
        }
        // Called joined event
        new CorePlayerJoinedEvent(corePlayer).call();
    }

    @Override
    public void playerLoggedOut(Player player) {
        if (player == null) return;
        removePlayer(player.getUniqueId());
    }

    @Override
    public void removePlayer(UUID uuid) {
        if (uuid == null) return;
        CPlayer cPlayer = getPlayer(uuid);
        if (cPlayer == null) return;
        Core.runTask(() -> {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                cPlayer.getScoreboard().removePlayerTag(otherPlayer);
                otherPlayer.getScoreboard().removePlayerTag(cPlayer);
            }
        });
        cPlayer.resetManagers();
        cPlayer.setStatus(PlayerStatus.LEFT);
        onlinePlayers.remove(cPlayer.getUniqueId());
    }

    @Override
    public void broadcastMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Override
    public CPlayer getPlayer(UUID playerUUID) {
        return onlinePlayers.get(playerUUID);
    }

    @Override
    public CPlayer getPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return getPlayer(player.getUniqueId());
    }

    @Override
    public CPlayer getPlayer(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p == null)
            return null;
        return getPlayer(p);
    }

    @Override
    public List<CPlayer> getOnlinePlayers() {
        if (onlinePlayers == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(onlinePlayers.values());
    }

    @Override
    public void displayRank(CPlayer player) {
        // Set op if the player should be
        boolean op = player.getRank().isOp();
        if (player.isOp() != op) {
            player.setOp(op);
        }

        player.getScoreboard().setupPlayerTags();
        defaultScoreboard.setup(player);
        Core.runTaskLater(() -> {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                if (player.getScoreboard() != null) player.getScoreboard().addPlayerTag(otherPlayer);
                if (otherPlayer.getScoreboard() != null) otherPlayer.getScoreboard().addPlayerTag(player);
            }
        }, 20L);
    }
}
