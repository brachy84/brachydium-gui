package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiScreen extends Screen implements GuiHelper {

    private final Gui gui;
    private final List<Interactable> interactables = new ArrayList<>();
    private Interactable focused;

    protected GuiScreen(Gui gui) {
        super(new LiteralText(""));
        this.gui = gui;
        this.gui.setScreen(this);
        this.gui.init();
    }

    @Override
    public void onClose() {
        super.onClose();
        gui.close();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        Pos2d pos = new Pos2d(mouseX, mouseY);
        gui.render(matrices, pos, delta);
        gui.renderForeground(matrices, pos, delta);
    }

    @Nullable
    public Interactable getHoveredInteractable(Pos2d pos) {
        Interactable topWidget = null;
        for (Interactable interactable : interactables.stream().filter(interactable ->
                interactable.isMouseOver(pos)).collect(Collectors.toSet())) {
            if (interactable instanceof Widget widget) {
                if (topWidget == null) {
                    topWidget = interactable;
                    continue;
                }
                if (widget.getLayer() > ((Widget)topWidget).getLayer()) {
                    topWidget = interactable;
                }
            }
        }
        return topWidget;
    }

    public int getInteractableId(Interactable interactable) {
        return interactable.getWidgetId();
    }

    public void setFocused(Interactable interactable) {
        focused = interactable;
    }

    public boolean isFocused(Interactable interactable) {
        return focused != null && interactable == focused;
    }

    @Nullable
    public Interactable getFocusedWidget() {
        return focused;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if (focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_CLICKED, buf);
            setFocused(focused);
            setDragging(true);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if (focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_RELEASED, buf);

            setDragging(false);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.getFocusedWidget() != null && this.isDragging()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);
            buf.writeDouble(deltaX);
            buf.writeDouble(deltaY);

            ClientPlayNetworking.send(Networking.MOUSE_DRAGGED, buf);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if (focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeDouble(amount);

            ClientPlayNetworking.send(Networking.MOUSE_SCROLLED, buf);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // TODO implement
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // TODO implement
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // TODO implement
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        //interactables.forEach(interactable -> interactable.onMouseMoved(new Point(mouseX, mouseY)));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return gui.getBounds().isInBounds(new Pos2d(mouseX, mouseY));
    }

    @Deprecated
    @Nullable
    @Override
    public final Element getFocused() {
        return null;
    }

    @Deprecated
    @Override
    public final void setFocused(@Nullable Element focused) {
    }

    public Gui getGui() {
        return gui;
    }

    @Override
    public float getZ() {
        return 0;
    }

    @Override
    public Pos2d getMousePos() {
        return null;
    }

    @Override
    public void setZ(float z) {
    }

}
