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

package ua.nanit.limbo.connection;

import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.configuration.PacketFinishConfiguration;
import ua.nanit.limbo.protocol.packets.configuration.PacketKnownPacks;
import ua.nanit.limbo.protocol.packets.login.PacketLoginAcknowledged;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginRequest;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginResponse;
import ua.nanit.limbo.protocol.packets.login.PacketLoginStart;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.UuidUtil;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class PacketHandler {

    private final LimboServer server;

    public void handle(@NonNull ClientConnection conn, @NonNull PacketHandshake packet) {
        conn.updateVersion(packet.getVersion());
        conn.updateState(packet.getNextState());

        Log.debug("Pinged from %s [%s]", conn.getAddress(),
                conn.getClientVersion().toString());

        if (this.server.getConfig().getInfoForwarding().isLegacy()) {
            String[] split = packet.getHost().split("\00");

            if (split.length == 3 || split.length == 4) {
                conn.setAddress(split[1]);
                conn.getGameProfile().setUuid(UuidUtil.fromString(split[2]));
            } else {
                conn.disconnectLogin("You've enabled player info forwarding. You need to connect with proxy");
            }
        } else if (this.server.getConfig().getInfoForwarding().isBungeeGuard()) {
            if (!conn.checkBungeeGuardHandshake(packet.getHost())) {
                conn.disconnectLogin("Invalid BungeeGuard token or handshake format");
            }
        }
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketStatusRequest packet) {
        conn.sendPacket(new PacketStatusResponse(server));
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketStatusPing packet) {
        conn.sendPacketAndClose(packet);
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginStart packet) {
        if (server.getConfig().getMaxPlayers() > 0 &&
                server.getConnections().getCount() >= server.getConfig().getMaxPlayers()) {
            conn.disconnectLogin("Too many players connected");
            return;
        }

        if (!conn.getClientVersion().isSupported()) {
            conn.disconnectLogin("Unsupported client version");
            return;
        }

        if (server.getConfig().getInfoForwarding().isModern()) {
            int loginId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            PacketLoginPluginRequest request = new PacketLoginPluginRequest();

            request.setMessageId(loginId);
            request.setChannel(LimboConstants.VELOCITY_INFO_CHANNEL);
            request.setData(Unpooled.EMPTY_BUFFER);

            conn.setVelocityLoginMessageId(loginId);
            conn.sendPacket(request);
            return;
        }

        if (!server.getConfig().getInfoForwarding().isModern()) {
            conn.getGameProfile().setUsername(packet.getUsername());
            conn.getGameProfile().setUuid(UuidUtil.getOfflineModeUuid(packet.getUsername()));
        }

        conn.fireLoginSuccess();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginPluginResponse packet) {
        if (server.getConfig().getInfoForwarding().isModern()
                && packet.getMessageId() == conn.getVelocityLoginMessageId()) {

            if (!packet.isSuccessful() || packet.getData() == null) {
                conn.disconnectLogin("You need to connect with Velocity");
                return;
            }

            if (!conn.checkVelocityKeyIntegrity(packet.getData())) {
                conn.disconnectLogin("Can't verify forwarded player info");
                return;
            }

            // Order is important
            conn.setAddress(packet.getData().readString());
            conn.getGameProfile().setUuid(packet.getData().readUuid());
            conn.getGameProfile().setUsername(packet.getData().readString());

            conn.fireLoginSuccess();
        }
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginAcknowledged packet) {
        conn.onLoginAcknowledgedReceived();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketFinishConfiguration packet) {
        conn.spawnPlayer();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketKnownPacks packet) {
        conn.onKnownPacksReceived();
    }
}
