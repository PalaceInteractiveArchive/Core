/*
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets.server.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class WrapperPlayServerBlockAction extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_ACTION;

    public WrapperPlayServerBlockAction() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerBlockAction(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Location.
     * <p>
     * Notes: block Coordinates
     *
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Set Location.
     *
     * @param block - block value.
     */
    public void setLocation(Block block) {
        Location location = block.getLocation();
        handle.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    /**
     * Retrieve Action ID.
     * <p>
     * Notes: varies depending on block - see Block_Actions
     *
     * @return The current Byte 1
     */
    public int getActionID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Action ID.
     *
     * @param value - new value.
     */
    public void setActionID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve Action Param.
     * <p>
     * Notes: varies depending on block - see Block_Actions
     *
     * @return The current Byte 2
     */
    public int getActionParam() {
        return handle.getIntegers().read(1);
    }

    /**
     * Set Action Param.
     *
     * @param value - new value.
     */
    public void setActionParam(int value) {
        handle.getIntegers().write(1, value);
    }

    /**
     * Retrieve Block Type.
     * <p>
     * Notes: the block type for the block
     *
     * @return The current Block Type
     */
    public Material getBlockType() {
        return handle.getBlocks().read(0);
    }

    /**
     * Set Block Type.
     *
     * @param value - new value.
     */
    public void setBlockType(Material value) {
        handle.getBlocks().write(0, value);
    }
}
