package brachy84.brachydium.gui.test;

import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.internal.BlockEntityUiFactory;
import brachy84.brachydium.gui.internal.BlockEntityWithGui;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends Block implements BlockEntityProvider {

    public TestBlock() {
        super(FabricBlockSettings.of(Material.METAL));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        System.out.println("Creating be");
        return new TestBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient()) {
            System.out.println("OnUse");
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof TestBlockEntity) {
                boolean opened = BlockEntityUiFactory.INSTANCE.openUI((TestBlockEntity)be, (ServerPlayerEntity) player);
                if(opened)
                    return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }
}
