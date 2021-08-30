package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.Draggable;
import brachy84.brachydium.gui.api.ITexture;
import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.widgets.ResourceSlotWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public final class CursorWidget extends ResourceSlotWidget<ItemStack> {

    private ItemStack stack;
    @Nullable
    private Draggable draggable;
    private Pos2d clickedRelativPos;

    public CursorWidget() {
        setSize(ItemSlotWidget.SIZE);
        stack = ItemStack.EMPTY;
    }

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        matrices.push();
        matrices.translate(0, 0, 500);
        if (!stack.isEmpty())
            renderResource(helper, matrices);
        if (draggable != null) {
            Pos2d mousePos = helper.getMousePos();
            Pos2d draggablePos = ((Widget) draggable).getPos();
            matrices.translate(mousePos.x - draggablePos.x - clickedRelativPos.x, mousePos.y - draggablePos.y - clickedRelativPos.y, 0);
            draggable.renderMovingState(helper, matrices, delta);
            if (draggable.shouldRenderChildren()) {
                ((Widget) draggable).getChildren().forEach(widget -> widget.drawWidget(matrices, delta, mousePos, false));
                ((Widget) draggable).getChildren().forEach(widget -> widget.drawWidget(matrices, delta, mousePos, true));
            }
        }
        matrices.pop();
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
        stack = resource;
        return true;
    }

    @Override
    public Pos2d getPos() {
        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        Mouse mouse = MinecraftClient.getInstance().mouse;
        return new Pos2d(mouse.getX() / scaleFactor, mouse.getY() / scaleFactor);
    }

    @Override
    public Pos2d getRelativePos() {
        return getPos();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty() && draggable == null;
    }

    @Override
    public ITexture getFallbackTexture() {
        return null;
    }

    private <T extends Widget & Interactable> T getFocused() {
        return (T) getGui().getScreen().getFocusedWidget();
    }

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        if (getFocused() instanceof Draggable draggable && isEmpty()) {
            if (!draggable.onDragStart(buttonId))
                return ActionResult.PASS;
            ((Widget) draggable).setEnabled(false);
            draggable.setState(Draggable.State.MOVING);
            this.draggable = draggable;
            this.clickedRelativPos = getPos().subtract(((Widget) draggable).getPos());
            return ActionResult.SUCCESS;
        } else if (draggable != null) {
            Pos2d mousePos = getPos();
            boolean successful;
            Widget topWidget = null;
            for(Widget widget : getGui().getMatchingWidgets(widget -> widget.isInBounds(mousePos))) {
                if(topWidget == null) {
                    topWidget = widget;
                    continue;
                }
                if(widget.getLayer() > topWidget.getLayer())
                    topWidget = widget;
            }
            if (successful = draggable.canDropHere(topWidget, mousePos, getGui().getBounds().isInBounds(mousePos))) {
                ((Widget) draggable).setAbsolutePos(mousePos.subtract(clickedRelativPos));
                getGui().reBuild();
            }
            draggable.onDragEnd(successful);
            ((Widget) draggable).setEnabled(true);
            draggable.setState(Draggable.State.IDLE);
            draggable = null;
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
