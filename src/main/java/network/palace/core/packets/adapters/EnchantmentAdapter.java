package network.palace.core.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import network.palace.core.Core;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * The type Enchantment adapter.
 */
public class EnchantmentAdapter extends PacketAdapter {

    /**
     * Instantiates a new Enchantment adapter.
     */
    public EnchantmentAdapter() {
        super(Core.getInstance(), ListenerPriority.HIGH, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            addGlow(new ItemStack[]{event.getPacket().getItemModifier().read(0)});
        } else {
            addGlow(event.getPacket().getItemArrayModifier().read(0));
        }
    }

    private void addGlow(ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null) {
                if (stack.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 69) {
                    NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
                    compound.put(NbtFactory.ofList("ench"));
                }
            }
        }
    }
}
