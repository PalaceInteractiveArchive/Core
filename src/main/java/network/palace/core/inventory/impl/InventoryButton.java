package network.palace.core.inventory.impl;

import lombok.Getter;
import network.palace.core.inventory.ButtonCriteria;
import network.palace.core.inventory.InventoryButtonInterface;
import network.palace.core.inventory.InventoryClick;
import network.palace.core.player.CPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryButton implements InventoryButtonInterface {

    @Getter private ItemStack stack;
    @Getter private InventoryClick click;
    @Getter private ButtonCriteria criteria;

    public InventoryButton(Material material, InventoryClick click) {
        this(material, click, new DefaultButtonCriteria());
    }

    public InventoryButton(Material material, InventoryClick click, ButtonCriteria criteria) {
        this.stack = new ItemStack(material, 1);
        this.click = click;
        this.criteria = criteria;
    }

    public InventoryButton(ItemStack stack, InventoryClick click) {
        this(stack, click, new DefaultButtonCriteria());
    }

    public InventoryButton(ItemStack stack, InventoryClick click, ButtonCriteria criteria) {
        this.stack = stack;
        this.click = click;
        this.criteria = criteria;
    }

    @Override
    public boolean isVisible(CPlayer player) {
        return criteria.isVisible(player);
    }
}
