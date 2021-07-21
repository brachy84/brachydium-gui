package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.UIHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class BlockEntityWithGui extends BlockEntity implements UIHolder {

    public BlockEntityWithGui(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
