package brachy84.brachydium.gui.internal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityUiFactory extends UIFactory<BlockEntityWithGui> {

    public static final BlockEntityUiFactory INSTANCE = new BlockEntityUiFactory();

    private BlockEntityUiFactory() {
    }

    @Override
    public Identifier getId() {
        return new Identifier("brachydium", "default_block_entity_ui_factory");
    }

    @Override
    public BlockEntityWithGui readHolderFromSyncData(PacketByteBuf syncData) {
        BlockPos pos = syncData.readBlockPos();
        World world = MinecraftClient.getInstance().world;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BlockEntityWithGui)
            return (BlockEntityWithGui) be;
        throw new IllegalStateException("BlockEntity at pos " + pos + " is not of type UIHolder");
    }

    @Override
    public void writeHolderToSyncData(PacketByteBuf syncData, BlockEntityWithGui holder) {
        syncData.writeBlockPos(holder.getPos());
    }
}
