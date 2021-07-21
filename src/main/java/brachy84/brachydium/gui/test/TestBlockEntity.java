package brachy84.brachydium.gui.test;

import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.Centered;
import brachy84.brachydium.gui.api.widgets.Layout.CrossAxisAlignment;
import brachy84.brachydium.gui.api.widgets.SpriteWidget;
import brachy84.brachydium.gui.internal.BlockEntityWithGui;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.api.widgets.RootWidget;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class TestBlockEntity extends BlockEntityWithGui {

    public static BlockEntityType<TestBlockEntity> TYPE;

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public @NotNull Identifier getUiId() {
        return null;
    }

    @Override
    public @NotNull Gui createUi(PlayerEntity player) {
        TextureArea background = TextureArea.fullImage(new Identifier("brachydium", "gui/base/background"), new Size(176, 166));
        TextureArea slot = TextureArea.fullImage(new Identifier("brachydium", "gui/base/slot"), new Size(18, 18));
        return Gui.builder(player, background)
                .bindPlayerInventory(7, Alignment.BottomCenter)
                .build();
    }
}
