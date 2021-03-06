package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.Widget;
import brachy84.brachydium.gui.api.helpers.Draggable;
import brachy84.brachydium.gui.api.helpers.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public final class CursorWidget extends Widget implements Interactable {

    private ItemStack stack = ItemStack.EMPTY;
    @Nullable
    private Draggable draggable;
    private Pos2d clickedRelativPos;

    public CursorWidget() {
        setSize(ItemSlotWidget.SIZE);
    }

    @Override
    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        matrices.push();
        matrices.translate(0, 0, 500);
        if (draggable != null) {
            Pos2d draggablePos = ((Widget) draggable).getPos();
            matrices.translate(mousePos.x - draggablePos.x - clickedRelativPos.x, mousePos.y - draggablePos.y - clickedRelativPos.y, 0);
            draggable.renderMovingState(matrices, mousePos, delta);
            if (draggable.shouldRenderChildren()) {
                ((Widget) draggable).getChildren().forEach(widget -> widget.drawWidget(matrices, delta, mousePos, false));
                ((Widget) draggable).getChildren().forEach(widget -> widget.drawWidget(matrices, delta, mousePos, true));
            }
        }
        matrices.pop();
    }

    @Override
    public void tick() {
        if (getGui().isClient()) {
            if (!ItemStack.areEqual(stack, MinecraftClient.getInstance().player.currentScreenHandler.getCursorStack())) {
                stack = MinecraftClient.getInstance().player.currentScreenHandler.getCursorStack();
                syncToServer(0, buf -> buf.writeItemStack(stack));
            }
        }
    }

    public void setCursorStack(ItemStack stack) {
        this.stack = stack;
        if (getGui().isClient()) {
            MinecraftClient.getInstance().player.currentScreenHandler.setCursorStack(stack);
            syncToServer(0, buf -> buf.writeItemStack(getGui().getCursorStack()));
        } else {
            syncToClient(getGui().player, 0, buf -> buf.writeItemStack(stack));
        }
    }

    public ItemStack getCursorStack() {
        return stack;
    }

    @Override
    public void renderForeground(MatrixStack matrices, Pos2d mousePos, float delta) {
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

    private <T extends Widget & Interactable> T getFocused() {
        return (T) getGui().getScreen().getFocusedWidget();
    }

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        if (draggable == null && getFocused() instanceof Draggable draggable && getCursorStack().isEmpty()) {
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
            for (Widget widget : getGui().getMatchingWidgets(widget -> widget.isInBounds(mousePos))) {
                if (topWidget == null) {
                    topWidget = widget;
                    continue;
                }
                if (widget.getLayer() > topWidget.getLayer())
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

    @Override
    public void readServerData(int id, PacketByteBuf buf) {
        if (id == 0) {
            this.stack = buf.readItemStack();
            MinecraftClient.getInstance().player.currentScreenHandler.setCursorStack(stack);
        }
    }

    @Override
    public void readClientData(int id, PacketByteBuf buf) {
        if (id == 0)
            this.stack = buf.readItemStack();
    }
}
