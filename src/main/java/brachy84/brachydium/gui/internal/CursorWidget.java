package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import brachy84.brachydium.gui.api.widgets.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class CursorWidget extends ResourceSlotWidget<ItemStack> {

    private ItemStack stack;
    @Nullable
    private Draggable draggable;

    public CursorWidget() {
        setSize(ItemSlotWidget.SIZE);
        stack = ItemStack.EMPTY;
    }

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        if(!stack.isEmpty())
            renderResource(helper, matrices);
        if(draggable != null)
            draggable.renderMovingState(helper, matrices, delta);
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
    public void renderResource(IGuiHelper helper, MatrixStack matrices) {
        helper.drawItem(matrices, getResource(), helper.getMousePos().add(-8, -8));
    }

    @Override
    public void renderForeground(IGuiHelper helper, MatrixStack matrices, float delta) {
    }

    @Override
    public void renderTooltip(IGuiHelper helper, MatrixStack matrices, float delta) {
    }

    @Override
    public ItemStack getResource() {
        return stack;
    }

    @Override
    public boolean setResource(ItemStack resource) {
        if (resource == null) return false;
        System.out.println("Set cursor stack to " + resource);
        stack = resource;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty() && draggable == null;
    }

    @Override
    public IDrawable getFallbackTexture() {
        return null;
    }

    private  <T extends Widget & Interactable> T getFocused() {
        return (T) getGui().getScreen().getFocusedWidget();
    }
}
