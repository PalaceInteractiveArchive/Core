/*
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets.server;

import network.palace.core.packets.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * The type Wrapper play server chat.
 */
public class WrapperPlayServerChat extends AbstractPacket {

    /**
     * The constant TYPE.
     */
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    /**
     * Instantiates a new Wrapper play server chat.
     */
    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieve the chat message.
     * <p>
     * Limited to 32767 bytes
     *
     * @return The current message
     */
    public WrappedChatComponent getMessage() {
        return handle.getChatComponents().read(0);
    }

    /**
     * Set the message.
     *
     * @param value - new value.
     */
    public void setMessage(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }

    /**
     * Retrieve Position.
     * <p>
     * Notes: 0 - Chat (chat box) ,1 - System Message (chat box), 2 - Above action bar
     *
     * @return The current Position
     */
    public Position getPosition() {
        return Position.values()[handle.getBytes().read(0).intValue()];
    }

    /**
     * Set Position.
     *
     * @param value - new value.
     */
    public void setPosition(Position value) {
        handle.getBytes().write(0, value.value);
    }

    /**
     * The enum Position.
     */
    public enum Position {
        /**
         * Chat position.
         */
        CHAT((byte) 0),
        /**
         * Chat system position.
         */
        CHAT_SYSTEM((byte) 1),
        /**
         * Action bar position.
         */
        ACTION_BAR((byte) 2);

        private final byte value;

        Position(byte value) {
            this.value = value;
        }
    }
}
