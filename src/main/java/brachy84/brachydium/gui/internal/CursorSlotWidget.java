package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class CursorSlotWidget extends ResourceSlotWidget<ItemStack> {

    private ItemStack stack;

    public CursorSlotWidget() {
        super(ItemSlotWidget.SIZE, Pos2d.ZERO);
        stack = ItemStack.EMPTY;
    }

    @Override
    public @Nullable RenderObject getRenderObject() {
        return ((matrices, delta) -> renderResource(matrices));
    }

    @Override
    public void readData(PacketByteBuf data) {
        setResource(data.readItemStack());
    }

    @Override
    public void writeData(PacketByteBuf data) {
        data.writeItemStack(getResource());
    }

    @Override
    public boolean isMouseOver(Pos2d pos) {
        return true;
    }

    @Override
    public void renderResource(MatrixStack matrices) {
        guiHelper.drawItem(matrices, getResource(), guiHelper.getMousePos());
    }

    @Override
    public ItemStack getResource() {
        return stack;
    }

    @Override
    public boolean setResource(ItemStack resource) {
        if (resource == null) return false;
        stack = resource;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public TextureArea getFallbackTexture() {
        return null;
    }
}
