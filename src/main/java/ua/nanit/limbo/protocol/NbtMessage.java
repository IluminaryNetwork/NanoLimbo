package ua.nanit.limbo.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.nbt.CompoundBinaryTag;

@AllArgsConstructor
@Getter
@Setter
public class NbtMessage {

    private String json;
    private CompoundBinaryTag tag;
}
