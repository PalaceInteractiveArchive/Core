package network.palace.core.pathfinding.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGearMob extends AbstractMob {

    private ItemStack itemInHand;
    private ItemStack[] armor = new ItemStack[4];
    private final Set<Integer> gearToUpdate = new HashSet<>();

    public AbstractGearMob(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    public void setHelmet(ItemStack stack) {
        armor[3] = stack;
        gearToUpdate.add(4);
    }

    public void setChestplate(ItemStack stack) {
        armor[2] = stack;
        gearToUpdate.add(3);
    }

    public void setLeggings(ItemStack stack) {
        armor[1] = stack;
        gearToUpdate.add(2);
    }

    public void setBoots(ItemStack stack) {
        armor[0] = stack;
        gearToUpdate.add(1);
    }

    public void setArmor(ItemStack[] stacks) {
        if (stacks.length != 4) throw new IllegalArgumentException("You must pass four items as armor!");
        armor = stacks;
        gearToUpdate.addAll(Arrays.asList(1,2,3,4));
    }

    public void setItemInHand(ItemStack stack) {
        itemInHand = stack;
        gearToUpdate.add(0);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        updateEquipment();
    }

    private void updateEquipment() {
        for (int x = 0; x <= 4; x++) {
            if (!gearToUpdate.contains(x)) continue;
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, id);
            packet.getShorts().write(1, (short) x);
            switch (x) {
                case 0:
                    packet.getItemModifier().write(2, itemInHand);
                    break;
                default:
                    packet.getItemModifier().write(2, armor[x-1]);
            }
            for (CPlayer player : getTargets()) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        gearToUpdate.clear();
    }
}
