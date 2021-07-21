package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.IDrawable;
import brachy84.brachydium.gui.api.IGuiHelper;
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
        setSize(ItemSlotWidget.SIZE);
        stack = ItemStack.EMPTY;
    }

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        //matrices.push();
        //matrices.translate(-8, -8, 500);
        renderResource(helper, matrices);
        //matrices.pop();
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
        //System.out.println("MousePos " + helper.getMousePos());
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
        return stack.isEmpty();
    }

    @Override
    public IDrawable getFallbackTexture() {
        return null;
    }
}
