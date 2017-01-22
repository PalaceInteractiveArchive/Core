package network.palace.core.pathfinding.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.pathfinding.Point;
import network.palace.core.pathfinding.npc.packets.*;
import network.palace.core.player.CPlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public abstract class AbstractMob implements Observable<NPCObserver> {
    @Getter private Point location;
    @Getter private Integer headYaw;
    @Getter private final World world;
    private final Set<CPlayer> viewers;
    private final Set<NPCObserver> observers;
    @Getter private final WrappedDataWatcher dataWatcher;
    private WrappedDataWatcher lastDataWatcher;
    @Getter boolean spawned;
    @Getter protected final int id;
    private InteractWatcher listener;

    @Getter @Setter private String customName;
    @Setter private float health = 0;
    @Getter @Setter private boolean onFire, crouched, sprinting, blocking, invisible, showingNametag = true;

    private IdManager idManager;

    protected abstract EntityType getEntityType();
    public abstract float getMaximumHealth();
    protected void onUpdate() {}
    protected void onDataWatcherUpdate() {}

    // TODO: Do we actually need this?
    // {
    //     SoftNPCManager.getInstance().mobRefs.add(new WeakReference<>(this));
    // }

    public AbstractMob(Point location, World world, Set<CPlayer> observers, String title) {
        this.location = location.deepCopy();
        this.world = world;
        this.viewers = new HashSet<>();

        if (observers != null) this.viewers.addAll(observers);

        this.dataWatcher = new WrappedDataWatcher();
        this.observers = new HashSet<>();
        this.spawned = false;
        this.customName = title;

        this.idManager = new IdManager();

        this.id = idManager.getNextId();
    }

    private InteractWatcher createNewInteractWatcher() {
        listener = new InteractWatcher(this);
        return listener;
    }

    @Override
    public final void registerObservable(NPCObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public final void unregisterObservable(NPCObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public final ImmutableSet<NPCObserver> getObservers() {
        return ImmutableSet.copyOf(observers);
    }

    public final void addViewer(CPlayer player) {
        this.viewers.add(player);
        if (this.isSpawned()) forceSpawn(player);
    }

    public final void removeViewer(CPlayer player) {
        this.viewers.remove(player);
        if (this.isSpawned()) forceDespawn(player);
    }

    public final void makeGlobal() {
        this.viewers.clear();
    }

    public final Float getHealth() {
        return health == 0 ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    protected final CPlayer[] getTargets() {
        CPlayer[] cPlayers = (this.viewers.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.viewers).
                toArray(new CPlayer[this.viewers.size()]);
        CPlayer[] players = new CPlayer[cPlayers.length];

        int x = 0;
        for (int i = 0; i < cPlayers.length; i++) {
            CPlayer player = cPlayers[x];
            UUID uid = player.getLocation().getWorld().getUID();
            UUID uid1 = this.world != null ? world.getUID() : null;

            if (this.world != null && !uid.equals(uid1)) continue;
            players[x] = player;
            x++;
        }
        return x == 0 ? new CPlayer[]{} :  Arrays.copyOfRange(players, 0, x);
    }

    public final void spawn() {
        if (spawned) throw new IllegalStateException("This NPC is already spawned!");
        ProtocolLibrary.getProtocolManager().addPacketListener(createNewInteractWatcher());
        WrapperPlayServerSpawnEntityLiving packet = getSpawnPacket();
        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
        spawned = true;
    }

    private WrapperPlayServerEntityDestroy getDespawnPacket() {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntities(new int[]{id});
        return packet;
    }

    public final void despawn() {
        if (!spawned) throw new IllegalStateException("You cannot despawn something that you have not spawned!");
        WrapperPlayServerEntityDestroy packet = getDespawnPacket();
        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
    }

    public final void forceDespawn(CPlayer player) {
        getDespawnPacket().sendPacket(player);
    }

    public final void forceSpawn(CPlayer player) {
        getSpawnPacket().sendPacket(player);
    }

    protected final WrapperPlayServerSpawnEntityLiving getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(id);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setHeadPitch(location.getPitch()); //TODO check over this and set a default, or get an enum of different directions the head can be.
        packet.setHeadYaw(location.getYaw());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(getEntityType());
        return packet;
    }

    private WrapperPlayServerEntityStatus getStatusPacket(Integer status) {
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(id);
        packet.setEntityStatus(status);
        return packet;
    }

    protected final void playStatus(Set<CPlayer> players, Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        CPlayer[] targets = getTargets();
        for (CPlayer player : players) {
            packet.sendPacket(player);
        }
    }

    protected final void playStatus(Integer status) {
        WrapperPlayServerEntityStatus packet = getStatusPacket(status);
        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void playHurtAnimation() {
        playStatus(2);
    }
    public final void playDeadAnimation() {
        playStatus(3);
    }

    public final void playHurtAnimation(Set<CPlayer> players) {
        playStatus(players, 2);
    }
    public final void playDeadAnimation(Set<CPlayer> players) {
        playStatus(players, 3);
    }

    public final void update() {
        if (!spawned) spawn();
        updateDataWatcher();
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        List<WrappedWatchableObject> watchableObjects = new ArrayList<>();

        if (lastDataWatcher == null) {
            watchableObjects.addAll(dataWatcher.getWatchableObjects());
        } else {
            for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {
                Object object = lastDataWatcher.getObject(watchableObject.getIndex());
                if (object == null || !object.equals(watchableObject.getValue())) watchableObjects.add(watchableObject);
            }
        }

        for (WrappedWatchableObject watchableObject : watchableObjects) {
            Core.debugLog("Sending update on " + watchableObject.getIndex() + " for #" + id + " (" +
                    getClass().getSimpleName() + " ) =" + watchableObject.getValue() + " (" + watchableObject.getHandleType().getName() + ")");
        }

        packet.setEntityMetadata(watchableObjects);
        packet.setEntityId(id);

        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }

        lastDataWatcher = dataWatcher.deepClone();
        onUpdate();
    }

    public final void move(Point point) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that has yet to spawn!");
        final Point location = this.location;
        this.location = point;
        AbstractPacket packet;

        if (location.distanceSquared(point) <= 16) {
            WrapperPlayServerEntityMoveLook moveLookPacket = new WrapperPlayServerEntityMoveLook();
            moveLookPacket.setEntityId(id);
            moveLookPacket.setDx(point.getX() - location.getX());
            moveLookPacket.setDy(point.getY() - location.getY());
            moveLookPacket.setDz(point.getZ() - location.getZ());

            moveLookPacket.setPitch(point.getPitch());
            moveLookPacket.setYaw(point.getYaw());

            packet = moveLookPacket;
        } else {
            WrapperPlayServerEntityTeleport packet1 = new WrapperPlayServerEntityTeleport();
            packet1.setEntityId(id);
            packet1.setX(point.getX());
            packet1.setY(point.getY());
            packet1.setZ(point.getZ());
            packet1.setPitch(point.getPitch());
            packet1.setYaw(point.getYaw());
            packet = packet1;
        }
        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void addVelocity(Vector vector) {
        WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
        packet.setEntityId(id);

        packet.setVelocityX(vector.getX());
        packet.setVelocityY(vector.getY());
        packet.setVelocityZ(vector.getZ());

        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    public final void moveHead(byte parts) {
        if (!spawned) throw new IllegalStateException("You cannot modify the rotation of the head of a non-spawned entity!");
        headYaw = headYaw + parts;
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityId(id);
        packet.setHeadYaw(headYaw);
        for (CPlayer player : getTargets()) {
            packet.sendPacket(player);
        }
    }

    protected final void updateDataWatcher() {
        dataWatcher.setObject(6, getHealth());
        if (showingNametag) dataWatcher.setObject(3, (byte)1);
        else if (dataWatcher.getObject(3) != null) dataWatcher.removeObject(3);
        if (customName != null) dataWatcher.setObject(10, customName.substring(0, Math.min(customName.length(), 64)));
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(10);
        if (customName != null) dataWatcher.setObject(2, customName.substring(0, Math.min(customName.length(), 64)));
        else if (dataWatcher.getObject(10) != null) dataWatcher.removeObject(2);

        byte zeroByte = 0;
        if (onFire) zeroByte |= 0x01;
        if (crouched) zeroByte |= 0x02;
        if (sprinting) zeroByte |= 0x08;
        if (blocking) zeroByte |= 0x10;
        if (invisible) zeroByte |= 0x20;
        dataWatcher.setObject(0, zeroByte);
        onDataWatcherUpdate();
    }

    public final ImmutableSet<CPlayer> getViewers() {
        return ImmutableSet.copyOf(viewers);
    }

    private static class InteractWatcher extends PacketAdapter {
        private final AbstractMob watchingFor;

        public InteractWatcher(AbstractMob watchingFor) {
            super(Core.getInstance(), PacketType.Play.Client.USE_ENTITY);
            this.watchingFor = watchingFor;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
            if (packet.getTargetId() != watchingFor.getId()) return;
            CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(event.getPlayer());
            ClickAction clickAction = ClickAction.valueOf(packet.getMouse().name());
            for (NPCObserver npcObserver : watchingFor.getObservers()) {
                try {
                    npcObserver.onPlayerInteract(onlinePlayer, watchingFor, clickAction);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            event.setCancelled(true);
        }
    }
}
