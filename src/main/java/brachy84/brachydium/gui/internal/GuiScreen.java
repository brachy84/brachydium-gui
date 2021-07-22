package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.Widget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GuiScreen extends Screen implements GuiHelper {

    private final Gui gui;
    private final Interactable[] interactables;
    private Interactable focused;
    private long lastClick = 0;
    private Interactable lastFocusedClick;

    protected GuiScreen(Gui gui) {
        super(new LiteralText(""));
        this.gui = gui;
        this.gui.setScreen(this);
        this.gui.init();
        interactables = gui.getInteractables();
    }

    @Override
    public void onClose() {
        super.onClose();
        gui.close();
        ClientPlayNetworking.send(Networking.UI_CLOSE, PacketByteBufs.create());
    }

    @Override
    protected void init() {
        gui.onScreenResize();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        Pos2d pos = new Pos2d(mouseX, mouseY);
        gui.render(matrices, pos, delta);
    }

    public <T extends Widget & Interactable> List<T> getMatchingInteractables(Predicate<T> predicate) {
        List<T> interactables = new ArrayList<>();
        for (Interactable interactable : this.interactables) {
            if (predicate.test((T) interactable)) {
                interactables.add((T) interactable);
            }
        }
        return interactables;
    }

    @Nullable
    public Interactable getHoveredInteractable(Pos2d pos) {
        Interactable topWidget = null;
        for (Interactable interactable : getMatchingInteractables(widget -> ((Widget) widget).isInBounds(pos) && ((Widget) widget).isEnabled())) {
            if (interactable instanceof Widget widgetOld) {
                if (topWidget == null) {
                    topWidget = interactable;
                    continue;
                }
                if (widgetOld.getLayer() > ((Widget) topWidget).getLayer()) {
                    topWidget = interactable;
                }
            }
        }
        return topWidget;
    }

    public int getSyncedId(ISyncedWidget syncedWidget) {
        return gui.findIdForSyncedWidget(syncedWidget);
    }

    public int getFocusedId() {
        return getSyncedId(focused);
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
            long time = Util.getMeasuringTimeMs();
            boolean doubleClick = focused == lastFocusedClick && time - lastClick < 500;

            focused.onClick(pos, button, doubleClick);

            /*PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(getFocusedId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_CLICKED, buf);*/
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

            focused.onClickReleased(pos, button);
            /*PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(getFocusedId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_RELEASED, buf);*/

            setDragging(false);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.getFocusedWidget() != null && this.isDragging()) {

            getFocusedWidget().onMouseDragged(new Pos2d(mouseX, mouseY), button, deltaX, deltaY);
            /*PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(getFocusedId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);
            buf.writeDouble(deltaX);
            buf.writeDouble(deltaY);

            ClientPlayNetworking.send(Networking.MOUSE_DRAGGED, buf);*/
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if (focused != null) {

            focused.onScrolled(pos, amount);
            /*PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(getFocusedId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeDouble(amount);

            ClientPlayNetworking.send(Networking.MOUSE_SCROLLED, buf);*/
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (getFocusedWidget() != null) {
            getFocusedWidget().onKeyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (getFocusedWidget() != null) {
            getFocusedWidget().onKeyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (getFocusedWidget() != null) {
            getFocusedWidget().onCharTyped(chr, modifiers);
        }
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}