package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.npc.AbstractMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.packets.server.entity.WrapperPlayServerPlayerInfo;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @author Innectic
 * @since 3/29/2017
 */
public class MobPlayer extends AbstractMob {

    @Getter @Setter private boolean capeEnabled = true;
    @Getter @Setter private boolean jacketEnabled = true;
    @Getter @Setter private boolean leftSleeveEnabled = true;
    @Getter @Setter private boolean rightSleeveEnabled = true;
    @Getter @Setter private boolean leftPantLegEnabled = true;
    @Getter @Setter private boolean rightPantLegEnabled = true;
    @Getter @Setter private boolean hatEnabled = true;
    private MobPlayerTexture textureInfo;

    public MobPlayer(Point location, Set<CPlayer> observers, String title, MobPlayerTexture textureInfo) {
        super(location, observers, title);
        this.textureInfo = textureInfo;
    }

    @Override
    public void spawn() {
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = textureInfo.getWrappedSignedProperty(new WrappedGameProfile(getUuid(), getCustomName()));
        playerInfo.setData(Collections.singletonList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(getCustomName()))));
        Arrays.asList(getTargets()).forEach(playerInfo::sendPacket);

        super.spawn();

        setHeadYaw(getLocation().getLocation());
    }

    @Override
    public void despawn() {
        super.despawn();

        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        playerInfo.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(getUuid(), getCustomName()), 0, EnumWrappers.NativeGameMode.SURVIVAL, null)));
        Arrays.asList(getTargets()).forEach(playerInfo::sendPacket);
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
}
