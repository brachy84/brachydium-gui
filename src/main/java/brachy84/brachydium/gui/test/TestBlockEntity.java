package brachy84.brachydium.gui.test;

import brachy84.brachydium.gui.api.rendering.TextureArea;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.api.widgets.DraggableWidget;
import brachy84.brachydium.gui.api.widgets.SpriteWidget;
import brachy84.brachydium.gui.internal.BlockEntityWithGui;
import brachy84.brachydium.gui.api.Gui;
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
        return Gui.defaultBuilder(player)
                .bindPlayerInventory(EdgeInset.all(7), Alignment.BottomCenter)
                .widget(new DraggableWidget(
                        new SpriteWidget(TextureArea.fullImage("brachydiumgui", "icon"),32, 32))
                        .setAlignment(Alignment.TopCenter)
                        .setMargin(EdgeInset.top(16)))
                .build();
    }
}
