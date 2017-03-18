package network.palace.core.inventory.impl;

import lombok.*;
import network.palace.core.inventory.InventoryClick;
import network.palace.core.inventory.InventoryButtonInterface;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class InventoryButton implements InventoryButtonInterface {

    @Getter private ItemStack stack;
    @Getter private InventoryClick click;

    public InventoryButton(Material material, InventoryClick click) {
        this.stack = new ItemStack(material, 1);
        this.click = click;
    }
}
