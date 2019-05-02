package network.palace.core.menu;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public class MenuButton {

    @Getter private final int slot;
    @Getter private final ItemStack itemStack;
    @Getter private final Map<ClickType, Consumer<Player>> actions;

    public MenuButton(int slot, ItemStack itemStack) {
        this(slot, itemStack, ImmutableMap.of());
    }
}
