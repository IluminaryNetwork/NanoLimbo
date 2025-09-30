package ua.nanit.limbo.protocol.packets.configuration;

import lombok.Getter;
import lombok.Setter;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PacketUpdateTags implements PacketOut {

    private Map<String, Map<String, List<Integer>>> tags;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.tags.size());
        for (Map.Entry<String, Map<String, List<Integer>>> entry : this.tags.entrySet()) {
            msg.writeString(entry.getKey());

            Map<String, List<Integer>> subTags = entry.getValue();
            msg.writeVarInt(subTags.size());
            for (Map.Entry<String, List<Integer>> subEntry : subTags.entrySet()) {
                msg.writeString(subEntry.getKey());

                List<Integer> ids = subEntry.getValue();
                msg.writeVarInt(ids.size());
                for (int id : ids) {
                    msg.writeVarInt(id);
                }
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
