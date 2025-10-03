package ua.nanit.limbo.protocol.packets.play;

import lombok.Setter;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@Setter
public class PacketGameEvent implements PacketOut {

    private byte type;
    private float value;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeByte(type);
        msg.writeFloat(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
