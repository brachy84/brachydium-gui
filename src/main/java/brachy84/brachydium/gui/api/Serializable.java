package brachy84.brachydium.gui.api;

import net.minecraft.nbt.NbtCompound;

public interface Serializable {

    void toTag(NbtCompound tag);

    void fromTag(NbtCompound tag);
}
