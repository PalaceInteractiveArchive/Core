package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.*;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.npc.AbstractGearMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.*;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Stairs;

import java.util.*;

/**
 * @author Innectic
 * @since 3/29/2017
 */
public class MobPlayer extends AbstractGearMob {

    @Getter @Setter private boolean capeEnabled = true;
    @Getter @Setter private boolean jacketEnabled = true;
    @Getter @Setter private boolean leftSleeveEnabled = true;
    @Getter @Setter private boolean rightSleeveEnabled = true;
    @Getter @Setter private boolean leftPantLegEnabled = true;
    @Getter @Setter private boolean rightPantLegEnabled = true;
    @Getter @Setter private boolean hatEnabled = true;
    private MobPlayerTexture textureInfo;

    private List<UUID> needTabListRemoved = new ArrayList<>();
    @Getter private Location bed = null;
    private Location bobber = null;
    private int bobberEntityId;
    private boolean sitting;
    private MobArmorStand seat;
    private boolean sneaking;

    public MobPlayer(Point location, Set<CPlayer> observers, String title, MobPlayerTexture textureInfo) {
        super(location, observers, title);
        String tempUUID = UUID.randomUUID().toString();
        StringBuilder uuidBuilder = new StringBuilder(UUID.randomUUID().toString());
        uuidBuilder.setCharAt(14, '2');
        this.uuid = UUID.fromString(uuidBuilder.toString());
        this.textureInfo = textureInfo;
        if (title == null) {
            setCustomName(ChatColor.DARK_GRAY + getPrivateUsername());
        }
    }

    @Override
    public void spawn() {
        super.spawn();

        setHeadYaw(getLocation().getLocation());
    }

    @Override
    public void despawn() {
        super.despawn();

        if (hasConditionalName()) {
            for (CPlayer target : getTargets()) {
                WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
                playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                playerInfo.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(getUuid(), getCustomName(target)), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
                playerInfo.sendPacket(target);
                removeViewer(target);
            }
        } else {
            WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
            playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            playerInfo.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(getUuid(), getCustomName()), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
            Arrays.asList(getTargets()).forEach(p -> {
                playerInfo.sendPacket(p);
                removeViewer(p);
            });
        }
        if (isFishing()) setFishing(null);
    }

    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn();
        packet.setEntityID(entityId);
        packet.setPlayerUUID(uuid);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        return packet;
    }

    @Override
    protected void addViewer(CPlayer player) {
        super.addViewer(player);
        needTabListRemoved.add(player.getUniqueId());
    }

    @Override
    protected void removeViewer(CPlayer player) {
        super.removeViewer(player);
        needTabListRemoved.remove(player.getUniqueId());
    }

    @Override
    public void forceSpawn(CPlayer player, boolean update) {
        if (isViewer(player)) {
            return;
        }
        addViewer(player);
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = textureInfo.getWrappedSignedProperty(new WrappedGameProfile(getUuid(), getCustomName(player)));
        playerInfo.setData(Collections.singletonList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.ADVENTURE,
                WrappedChatComponent.fromText(getTabListName()))));
        playerInfo.sendPacket(player);

        getSpawnPacket().sendPacket(player);

        if (bed != null || bobber != null) {
            Core.runTaskLater(Core.getInstance(), () -> {
                if (bed != null) {
                    WrapperPlayServerBed bedPacket = getBedPacket();
                    viewers.forEach(bedPacket::sendPacket);
                }
                if (bobber != null) {
                    WrapperPlayServerSpawnEntity bobberPacket = getFishingPacket();
                    viewers.forEach(bobberPacket::sendPacket);
                }
            }, 10L);
        }

        if (update) update(new CPlayer[]{player}, true);

        sendYaw(new CPlayer[]{player});
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public float getMaximumHealth() {
        return 20;
    }

    @Override
    protected void onDataWatcherUpdate() {
        int skinPartsIndex = 13;
        byte value = 0;
        if (capeEnabled) value |= 0x01;
        if (jacketEnabled) value |= 0x02;
        if (leftSleeveEnabled) value |= 0x04;
        if (rightSleeveEnabled) value |= 0x08;
        if (leftPantLegEnabled) value |= 0x10;
        if (rightPantLegEnabled) value |= 0x20;
        if (hatEnabled) value |= 0x40;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(skinPartsIndex), value);
        super.onDataWatcherUpdate();
    }

    private String getPrivateUsername() {
        return getUuid().toString().substring(0, 8);
    }

    private String getTabListName() {
        return ChatColor.DARK_GRAY + "[NPC] " + getPrivateUsername();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        if (b) {
            Core.getSoftNPCManager().untrackHiddenPlayerMob(this);
        } else {
            Core.getSoftNPCManager().trackHiddenPlayerMob(this);
        }
    }

    public void removedFromTabList(UUID uuid) {
        needTabListRemoved.remove(uuid);
    }

    public boolean needsRemoveFromTabList(CPlayer player) {
        return needTabListRemoved.contains(player.getUniqueId());
    }

    public int getNeedsRemoved() {
        return needTabListRemoved.size();
    }

    public void setSleeping(Location bed) {
        this.bed = bed;
        WrapperPlayServerBed bedPacket = getBedPacket();
        viewers.forEach(bedPacket::sendPacket);
    }

    public boolean isSleeping() {
        return bed != null;
    }

    private WrapperPlayServerBed getBedPacket() {
        WrapperPlayServerBed bedPacket = new WrapperPlayServerBed();
        bedPacket.setEntityID(getEntityId());
        if (bed == null) {
            bedPacket.setLocation(null);
        } else {
            bedPacket.setLocation(new BlockPosition(bed.getBlockX(), bed.getBlockY(), bed.getBlockZ()));
        }
        return bedPacket;
    }

    public void setFishing(Location bobber) {
        this.bobber = bobber;
        if (bobber == null) {
            setMainHand(new ItemStack(Material.AIR));
            if (bobberEntityId != 0) {
                WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
                wrapper.setEntityIds(new int[]{bobberEntityId});
                Arrays.asList(getTargets()).forEach(wrapper::sendPacket);
            }
        } else {
            setMainHand(new ItemStack(Material.FISHING_ROD));
            this.bobberEntityId = Core.getSoftNPCManager().getIDManager().getNextID();
            WrapperPlayServerSpawnEntity bobberPacket = getFishingPacket();
            viewers.forEach(bobberPacket::sendPacket);
        }
    }

    public boolean isFishing() {
        return bobber != null;
    }

    private WrapperPlayServerSpawnEntity getFishingPacket() {
        if (bobber == null) return null;
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FISHING_FLOAT);
        wrapper.setEntityID(bobberEntityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(bobber.getX());
        wrapper.setY(bobber.getY());
        wrapper.setZ(bobber.getZ());
        wrapper.setObjectData(getEntityId());
        return wrapper;
    }

    public void setSitting(boolean sitting) {
        if (this.sitting == sitting) return;
        this.sitting = sitting;
        if (sitting) {
            Block block = getLocation().getLocation().getBlock();
            System.out.println(block == null);
            System.out.println(block.getType().name());
            System.out.println(block.getLocation().toString());
            if (block == null || block.getType().equals(Material.AIR)) {
                this.sitting = false;
                return;
            }
            Stairs stairs = null;
            if (block.getState().getData() instanceof Stairs) {
                stairs = (Stairs) block.getState().getData();
            }
            Location location = block.getLocation();
            location.add(0.5, -1.18888, 0.5);
            if (stairs != null && MiscUtil.DIRECTIONAL_YAW.containsKey(stairs.getDescendingDirection())) {
                location.setYaw(MiscUtil.DIRECTIONAL_YAW.get(stairs.getDescendingDirection()));
            }

            seat = new MobArmorStand(Point.of(location), null, "");
//            seat.setVisible(false);
            seat.setGravity(false);
            seat.setCustomNameVisible(false);
            seat.spawn();
            seat.addPassenger(this);
        } else if (seat != null) {
            seat.despawn();
            seat = null;
        }
    }

    public boolean isSitting() {
        return this.sitting;
    }

    @Override
    public void setCrouched(boolean crouched) {
        super.setCrouched(crouched);
        update(getTargets(), false);
    }
}
