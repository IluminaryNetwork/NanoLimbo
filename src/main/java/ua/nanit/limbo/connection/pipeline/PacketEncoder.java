/*
 * Copyright (C) 2020 Nan1t
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.Log;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private State.PacketRegistry registry;
    private Version version;

    public PacketEncoder() {
        updateVersion(Version.getMin());
        updateState(State.HANDSHAKING);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        if (registry == null) return;

        ByteMessage msg = new ByteMessage(out);
        int packetId;

        if (packet instanceof PacketSnapshot) {
            packetId = registry.getPacketId(((PacketSnapshot)packet).getPacketClass());
        } else {
            packetId = registry.getPacketId(packet.getClass());
        }

        if (packetId == -1) {
            Log.warning("Undefined packet class: %s[0x%s] (%d bytes)", packet.getClass().getName(), Integer.toHexString(packetId), msg.readableBytes());
            return;
        }

        msg.writeVarInt(packetId);

        try {
            packet.encode(msg, version);

            if (Log.isDebug()) {
                Log.debug("Sending %s[0x%s] packet (%d bytes)", packet.toString(), Integer.toHexString(packetId), msg.readableBytes());
            }
        } catch (Exception e) {
            Log.error("Cannot encode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
        }
    }

    public void updateVersion(Version version) {
        this.version = version;
    }

    public void updateState(State state) {
        this.registry = state.clientBound.getRegistry(version);
    }

}
