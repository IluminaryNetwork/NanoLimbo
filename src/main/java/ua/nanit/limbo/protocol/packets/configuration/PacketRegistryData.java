package ua.nanit.limbo.protocol.packets.configuration;

import lombok.Setter;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.MetadataWriter;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.world.DimensionRegistry;

@Setter
public class PacketRegistryData implements PacketOut {

    private DimensionRegistry dimensionRegistry;
    private MetadataWriter metadataWriter;

    @Override
    public void encode(ByteMessage msg, Version version) {
        if (metadataWriter != null) {
            if (version.moreOrEqual(Version.V1_20_5)) {
                metadataWriter.writeData(msg, version);
                return;
            }
        }
        msg.writeCompoundTag(dimensionRegistry.getCodec_1_20(), version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
