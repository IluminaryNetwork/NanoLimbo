package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@AllArgsConstructor
public class PacketSpawnPosition implements PacketOut {

    private long x;
    private long y;
    private long z;

    public PacketSpawnPosition() { }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeLong(encodePosition(x, y ,z));
        msg.writeFloat(0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static long encodePosition(long x, long y, long z) {
        return ((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
