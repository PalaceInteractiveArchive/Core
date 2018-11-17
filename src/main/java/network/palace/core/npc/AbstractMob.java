package network.palace.core.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.npc.status.Status;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.*;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;

public abstract class AbstractMob implements Observable<NPCObserver> {

    @Getter private Point location;
    @Getter private int headYaw;
    private final Set<CPlayer> visibleTo;
    private final Set<NPCObserver> observers;
    protected final Set<CPlayer> viewers;
    @Getter private final WrappedDataWatcher dataWatcher;
    private WrappedDataWatcher lastDataWatcher;
    //    private List<WrappedWatchableObject> lastDataWatcherObjects;
    @Getter boolean spawned;
    @Getter protected final int entityId;
    private InteractWatcher listener;

    private ConditionalName conditionalName;
    @Setter private String customName;
    @Setter private float health = 0;
    @Getter @Setter private boolean onFire, crouched, sprinting, visible = true, customNameVisible, gravity = true;
    @Getter @Setter protected UUID uuid;

    protected abstract EntityType getEntityType();

    public abstract float getMaximumHealth();

    protected void onUpdate() {
    }

    protected void onDataWatcherUpdate() {
    }

    {
        Core.getSoftNPCManager().getMobRefs().add(new WeakReference<>(this));
    }

    public AbstractMob(Point location, Set<CPlayer> observers, String title) {
        this.location = location.deepCopy();
        this.visibleTo = new HashSet<>();
        if (observers != null) this.visibleTo.addAll(observers);
        this.dataWatcher = new WrappedDataWatcher();
        this.observers = new HashSet<>();
        this.viewers = new HashSet<>();
        this.spawned = false;
        if (title == null) {
            this.customName = "";
        } else {
            this.customName = title;
        }
        this.entityId = Core.getSoftNPCManager().getIDManager().getNextID();
    }

    private InteractWatcher createNewInteractWatcher() {
        return listener = new InteractWatcher(this);
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

    public final void addVisibleTo(CPlayer player) {
        this.visibleTo.add(player);
        if (this.isSpawned()) forceSpawn(player);
    }

    public final void removeVisibleTo(CPlayer player) {
        this.visibleTo.remove(player);
        if (this.isSpawned()) forceDespawn(player);
    }

    protected void addViewer(CPlayer player) {
        this.viewers.add(player);
    }

    protected void removeViewer(CPlayer player) {
        this.viewers.remove(player);
    }

    protected boolean isViewer(CPlayer player) {
        return this.viewers.contains(player);
    }

    public final void makeGlobal() {
        this.visibleTo.clear();
    }

    public final Float getHealth() {
        return health == 0 ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    protected final CPlayer[] getTargets() {
        CPlayer[] cPlayers = (this.visibleTo.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.visibleTo).toArray(new CPlayer[this.visibleTo.size()]);
        CPlayer[] players = new CPlayer[cPlayers.length];
        int x = 0;

        for (CPlayer player : cPlayers) {
            UUID uid = player.getLocation().getWorld().getUID();
            UUID uid1 = this.location.getWorld() != null ? location.getWorld().getUID() : null;

            if (this.location.getWorld() != null && !uid.equals(uid1)) continue;
            players[x] = player;
            x++;
        }
        return x == 0 ? new CPlayer[]{} : Arrays.copyOfRange(players, 0, x);
    }

    public void spawn() {
        if (spawned) return;
        ProtocolLibrary.getProtocolManager().addPacketListener(createNewInteractWatcher());
        spawned = true;
        CPlayer[] targets = getTargets();
        Arrays.asList(targets).forEach(t -> this.forceSpawn(t, false));
        update(targets, true);
    }

    private WrapperPlayServerEntityDestroy getDespawnPacket() {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntityIds(new int[]{entityId});
        return packet;
    }

    public void despawn() {
        if (!spawned) return;
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
        Arrays.asList(getTargets()).forEach(this::forceDespawn);
    }

    public final void forceDespawn(CPlayer player) {
        if (!isViewer(player)) {
            return;
        }
        getDespawnPacket().sendPacket(player);
        removeViewer(player);
    }

    public final void forceSpawn(CPlayer player) {
        forceSpawn(player, true);
    }

    public void forceSpawn(CPlayer player, boolean update) {
        if (!isViewer(player)) {
            getSpawnPacket().sendPacket(player);
            addViewer(player);
        }

        if (update) update(new CPlayer[]{player}, true);
    }

    protected final AbstractPacket getSpawnPacket() {
        if (getEntityType() == EntityType.PLAYER) {
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
        } else {
            WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
            packet.setEntityID(entityId);
            packet.setX(location.getX());
            packet.setY(location.getY());
            packet.setZ(location.getZ());
            packet.setPitch(location.getPitch());
            packet.setHeadPitch(location.getYaw());
            updateDataWatcher();
            packet.setMetadata(dataWatcher);
            packet.setType(getEntityType());
            return packet;
        }
    }

    private WrapperPlayServerEntityStatus getStatusPacket(int status) {
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityID(entityId);
        packet.setEntityStatus(status);
        return packet;
    }

    protected final void playStatus(Set<CPlayer> players, int status) {
        players.forEach(getStatusPacket(status)::sendPacket);
    }

    protected final void playStatus(int status) {
        Arrays.asList(getTargets()).forEach(getStatusPacket(status)::sendPacket);
    }

    public final void playHurtAnimation() {
        playStatus(Status.ENTITY_HURT);
    }

    public final void playDeadAnimation() {
        playStatus(Status.ENTITY_DEAD);
    }

    public final void playHurtAnimation(Set<CPlayer> players) {
        playStatus(players, Status.ENTITY_HURT);
    }

    public final void playDeadAnimation(Set<CPlayer> players) {
        playStatus(players, Status.ENTITY_DEAD);
    }

    public final void update() {
        update(false);
    }

    public final void update(boolean spawn) {
        update(getTargets(), spawn);
    }

    public final void update(CPlayer[] targets, boolean spawn) {
        try {
            if (!spawned) spawn();
            updateDataWatcher();
            List<WrappedWatchableObject> watchableObjects = new ArrayList<>();
            if (spawn) {
                watchableObjects.addAll(dataWatcher.getWatchableObjects());
            } else {
                if (lastDataWatcher == null) {
                    watchableObjects.addAll(dataWatcher.getWatchableObjects());
                } else {
                    for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {
                        Object object = lastDataWatcher.getObject(watchableObject.getIndex());
                        if (object == null || !object.equals(watchableObject.getValue())) {
                            watchableObjects.add(watchableObject);
                        }
                    }
                }
            }
            if (hasConditionalName()) {
                for (CPlayer target : targets) {
                    WrapperPlayServerEntityMetadata localPacket = new WrapperPlayServerEntityMetadata();
                    for (WrappedWatchableObject watchableObject : watchableObjects) {
                        if (watchableObject.getIndex() == 2) {
                            watchableObject.setValue(conditionalName.getCustomName(target));
                        }
                        Core.debugLog("Sending update on " + watchableObject.getIndex() + " for #" + entityId + " (" +
                                getClass().getSimpleName() + ") =" + watchableObject.getValue() + " (" + watchableObject.getHandleType().getName() + ")");
                    }
                    localPacket.setMetadata(watchableObjects);
                    localPacket.setEntityID(entityId);
                    localPacket.sendPacket(target);
                }
            } else {
                for (WrappedWatchableObject watchableObject : watchableObjects) {
                    Core.debugLog("Sending update on " + watchableObject.getIndex() + " for #" + entityId + " (" +
                            getClass().getSimpleName() + ") =" + watchableObject.getValue() + " (" + watchableObject.getHandleType().getName() + ")");
                }
                WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                packet.setMetadata(watchableObjects);
                packet.setEntityID(entityId);
                Arrays.asList(targets).forEach(packet::sendPacket);
                lastDataWatcher = deepClone(dataWatcher);
            }
            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WrappedDataWatcher deepClone(WrappedDataWatcher dataWatcher) {
        WrappedDataWatcher clone = new WrappedDataWatcher();
        if (MinecraftReflection.watcherObjectExists()) {
            dataWatcher.getWatchableObjects().forEach(object -> clone.setObject(object.getWatcherObject(), object));
        } else {
            dataWatcher.getWatchableObjects().forEach(object -> clone.setObject(object.getIndex(), object));
        }
        return clone;
    }

    public final void move(Point point) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that has yet to spawn!");
        final Point location = this.location;
        this.location = point;
        AbstractPacket packet;
        if (location.distanceSquared(point) <= 16) {
            WrapperPlayServerRelEntityMoveLook moveLookPacket = new WrapperPlayServerRelEntityMoveLook();
            moveLookPacket.setEntityID(entityId);
            moveLookPacket.setDx(point.getX() - location.getX());
            moveLookPacket.setDy(point.getY() - location.getY());
            moveLookPacket.setDz(point.getZ() - location.getZ());
            moveLookPacket.setPitch(point.getPitch());
            moveLookPacket.setYaw(point.getYaw());
            moveLookPacket.setOnGround(true);
            packet = moveLookPacket;
        } else {
            WrapperPlayServerEntityTeleport packet1 = new WrapperPlayServerEntityTeleport();
            packet1.setEntityID(entityId);
            packet1.setX(point.getX());
            packet1.setY(point.getY());
            packet1.setZ(point.getZ());
            packet1.setPitch(point.getPitch());
            packet1.setYaw(point.getYaw());
            packet = packet1;
        }
        Arrays.asList(getTargets()).forEach(packet::sendPacket);
    }

    public final void addVelocity(Vector vector) {
        WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
        packet.setEntityID(entityId);
        packet.setVelocityX(vector.getX());
        packet.setVelocityY(vector.getY());
        packet.setVelocityZ(vector.getZ());
        Arrays.asList(getTargets()).forEach(packet::sendPacket);
    }

    public final void setHeadYaw(Location location) {
        if (!spawned)
            throw new IllegalStateException("You cannot modify the rotation of the head of a non-spawned entity!");
        headYaw = (int) location.getYaw();
        sendYaw(getTargets());
    }

    public final void sendYaw(CPlayer[] players) {
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityID(entityId);
        packet.setHeadYaw(headYaw);
        Arrays.asList(players).forEach(packet::sendPacket);
    }

    protected final void updateDataWatcher() {
        int metadataIndex = 0;
        int customNameIndex = 2;
        int showNameTagIndex = 3;
        int noGravityIndex = 5;
        int healthIndex = 7;
        dataWatcher.setObject(ProtocolLibSerializers.getFloat(healthIndex), getHealth());
        if (customNameVisible) {
            dataWatcher.setObject(ProtocolLibSerializers.getBoolean(showNameTagIndex), true);
        } else if (dataWatcher.getObject(showNameTagIndex) != null) {
            dataWatcher.remove(showNameTagIndex);
        }
        if (customName != null) {
            dataWatcher.setObject(ProtocolLibSerializers.getString(customNameIndex), customName.substring(0, Math.min(customName.length(), 64)));
        } else if (dataWatcher.getObject(customNameIndex) != null) {
            dataWatcher.remove(customNameIndex);
        }
        if (!gravity) {
            dataWatcher.setObject(ProtocolLibSerializers.getBoolean(noGravityIndex), true);
        } else if (dataWatcher.getObject(noGravityIndex) != null) {
            dataWatcher.remove(noGravityIndex);
        }
        byte zeroByte = 0;
        if (onFire) zeroByte |= 0x01;
        if (crouched) zeroByte |= 0x02;
        if (sprinting) zeroByte |= 0x08;
        if (!visible) zeroByte |= 0x20;
        dataWatcher.setObject(ProtocolLibSerializers.getByte(metadataIndex), zeroByte);
        onDataWatcherUpdate();
    }

    public final ImmutableSet<CPlayer> getVisibleTo() {
        return ImmutableSet.copyOf(visibleTo);
    }

    public boolean canSee(CPlayer player) {
        if (visibleTo.isEmpty()) {
            return true;
        }
        return visibleTo.contains(player);
    }

    public boolean sameWorld(CPlayer player) {
        return location.getWorld().equals(player.getWorld());
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
            if (packet.getTargetID() != watchingFor.getEntityId()) return;
            CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(event.getPlayer());
            ClickAction clickAction = ClickAction.from(packet.getType().name());
            if (clickAction != null) {
                for (NPCObserver npcObserver : watchingFor.getObservers()) {
                    try {
                        npcObserver.onPlayerInteract(onlinePlayer, watchingFor, clickAction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public String getCustomName() {
        return getCustomName(null);
    }

    public String getCustomName(CPlayer player) {
        if (player == null || this.conditionalName == null || conditionalName.getCustomName(player) == null) {
            return customName;
        } else {
            return conditionalName.getCustomName(player);
        }
    }

    public void setConditionalName(ConditionalName conditionalName) {
        this.conditionalName = conditionalName;
    }

    public boolean hasConditionalName() {
        return this.conditionalName != null;
    }
}
