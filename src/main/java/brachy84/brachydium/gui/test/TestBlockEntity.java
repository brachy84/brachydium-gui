package brachy84.brachydium.gui.test;

import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.rendering.TextureArea;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.api.widgets.DraggableWidget;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.SpriteWidget;
import brachy84.brachydium.gui.internal.BlockEntityWithGui;
import brachy84.brachydium.gui.api.Gui;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class TestBlockEntity extends BlockEntityWithGui {

    public static BlockEntityType<TestBlockEntity> TYPE;
    private Inventory inventory;

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        inventory = new SimpleInventory(3);
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public @NotNull Gui createUi(PlayerEntity player) {
        return Gui.defaultBuilder(player)
                .bindPlayerInventory(EdgeInset.all(7), Alignment.BottomCenter)
                .widget(new DraggableWidget(
                        new SpriteWidget(TextureArea.fullImage("brachydiumgui", "icon"),32, 32))
                        .setAlignment(Alignment.TopCenter)
                        .setMargin(EdgeInset.top(16)))
                .widget(new ItemSlotWidget(inventory, 0, new Pos2d(4, 4)))
                .widget(new ItemSlotWidget(inventory, 1, new Pos2d(22, 4)))
                .widget(new ItemSlotWidget(inventory, 2, new Pos2d(40, 4)))
                .build();
    }
}
