package network.palace.core.inventory;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryButton {

    @Getter(AccessLevel.PROTECTED)
    private ItemStack stack;

    @Getter(AccessLevel.PROTECTED)
    private CoreInventoryClick click;

    public InventoryButton(Material material, CoreInventoryClick click) {
        this.stack = new ItemStack(material, 1);
        this.click = click;
    }

    public InventoryButton(ItemStack stack, CoreInventoryClick click) {
        this.stack = stack;
        this.click = click;
    }
}
