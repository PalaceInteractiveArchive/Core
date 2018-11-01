package network.palace.core.npc;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import network.palace.core.packets.server.entity.WrapperPlayServerEntityEquipment;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class AbstractGearMob extends AbstractMob {

    private ItemStack mainHand;
    private ItemStack offHand;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public AbstractGearMob(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    public void setMainHand(ItemStack stack) {
        mainHand = stack;
        updateSlot(ItemSlot.MAINHAND, mainHand);
    }

    public void setOffHand(ItemStack stack) {
        offHand = stack;
        updateSlot(ItemSlot.OFFHAND, offHand);
    }

    public void setHelmet(ItemStack stack) {
        helmet = stack;
        updateSlot(ItemSlot.HEAD, helmet);
    }

    public void setChestplate(ItemStack stack) {
        chestplate = stack;
        updateSlot(ItemSlot.CHEST, chestplate);
    }

    public void setLeggings(ItemStack stack) {
        leggings = stack;
        updateSlot(ItemSlot.LEGS, leggings);

    }

    public void setBoots(ItemStack stack) {
        boots = stack;
        updateSlot(ItemSlot.FEET, boots);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        updateSlot(ItemSlot.MAINHAND, mainHand);
        updateSlot(ItemSlot.OFFHAND, offHand);
        updateSlot(ItemSlot.HEAD, helmet);
        updateSlot(ItemSlot.CHEST, chestplate);
        updateSlot(ItemSlot.LEGS, leggings);
        updateSlot(ItemSlot.FEET, boots);
    }

    public void updateSlot(ItemSlot slot, ItemStack itemStack) {
        if (itemStack == null) return;
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
        wrapper.setEntityID(getEntityId());
        wrapper.setSlot(slot);
        wrapper.setItem(itemStack);
        for (CPlayer player : getTargets()) {
            wrapper.sendPacket(player);
        }
    }
}
