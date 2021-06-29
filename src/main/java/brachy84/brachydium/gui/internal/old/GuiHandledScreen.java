package brachy84.brachydium.gui.internal.old;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.GuiHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiHandledScreen extends HandledScreen<GuiScreenHandler> implements GuiHelper {

    private UIHolder uiHolder;
    private PlayerEntity player;
    private Interactable focused;
    //private ModularGuiOld guiOld;
    private Gui gui;
    private Interactable[] interactables = {};

    private float delta;
    private MatrixStack matrices;

    private long lastClickTime = 0L;
    private Interactable lastClickedInteractable = null;

    public GuiHandledScreen(GuiScreenHandler screenHandler, PlayerInventory inventory) {
        super(screenHandler, inventory, new LiteralText("H"));
        this.uiHolder = screenHandler.getUiHolder();
        this.player = inventory.player;
        this.gui = uiHolder.createUi(inventory.player);

        setZOffset(0);
        setMatrices(new MatrixStack());

        gui.init();
        initializeInteractables();
    }

    /*public ModularGuiHandledScreen(ModularGui gui) {
        super(new LiteralText("Hello"));
        this.gui = gui;
        this.guiHelper = new GuiHelperImpl(new MatrixStack());
        this.guiHelper.setZOffset(0);
    }*/

    @Override
    protected void init() {
        super.init();
        gui.resize(new Size(width, height));
        //screenShape = Shape.rect(new Size(width, height));
        backgroundHeight = (int) gui.getGuiSize().height;
        backgroundWidth = (int) gui.getGuiSize().width;
        init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        setMatrices(matrices);
        this.delta = delta;
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        gui.render(matrices, new Pos2d(mouseX, mouseY), delta);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        matrices.translate(-x, -y, 0);
        gui.renderForeground(matrices, new Pos2d(mouseX, mouseY), delta);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        super.renderTooltip(matrices, stack, x, y);
    }

    public void initializeInteractables() {
        interactables = gui.getInteractables();
    }

    /*public void forEachInteractableBelowMouse(Point point, Consumer<Interactable> consumer) {
        interactables.stream().filter(interactable -> interactable.isMouseOver(point) && isEnabled(interactable)).forEach(consumer);
    }*/

    @Override
    public void onClose() {
        super.onClose();
        gui.close();
    }

    public boolean isEnabled(Interactable interactable) {
        if(interactable instanceof Widget) {
            return ((Widget<?>) interactable).isEnabled();
        }
        return true;
    }

    public Stream<Interactable> filter(Predicate<Interactable> predicate) {
        return Stream.of(interactables).filter(predicate);
    }

    public Set<Interactable> filteredSet(Predicate<Interactable> predicate) {
        return filter(predicate).collect(Collectors.toSet());
    }

    @Nullable
    public Interactable getHoveredInteractable(Pos2d pos) {
        Interactable topWidget = null;
        for(Interactable interactable : filteredSet(interactable -> interactable.isMouseOver(pos) && isEnabled(interactable))) {
            if(interactable instanceof Widget) {
                if(topWidget == null) {
                    topWidget = interactable;
                    continue;
                }
                if(((Widget<?>) interactable).getLayer() > ((Widget<?>)topWidget).getLayer()) {
                    topWidget = interactable;
                }
            }
        }
        return topWidget;
    }

    public void setFocused(Interactable interactable) {
        focused = interactable;
    }

    public boolean isFocused(Interactable interactable) {
        return focused  != null && interactable == focused;
    }

    @Nullable
    public Interactable getFocusedWidget() {
        return focused;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        long time = Util.getMeasuringTimeMs();
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);
            buf.writeBoolean(time - this.lastClickTime < 250L && focused == lastClickedInteractable);

            ClientPlayNetworking.send(Networking.MOUSE_CLICKED, buf);
            setFocused(focused);
            setDragging(true);
        }
        lastClickTime = time;
        lastClickedInteractable = focused;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);

            ClientPlayNetworking.send(Networking.MOUSE_RELEASED, buf);

            setDragging(false);
        }
        /*Point point = new Point(mouseX, mouseY);
        Interactable top = getHoveredInteractable(point);
        if(top != null) {
            top.onClickReleased(point, button);
            return true;
        }*/
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.getFocusedWidget() != null && this.isDragging()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeInt(button);
            buf.writeDouble(deltaX);
            buf.writeDouble(deltaY);

            ClientPlayNetworking.send(Networking.MOUSE_DRAGGED, buf);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Pos2d pos = new Pos2d(mouseX, mouseY);
        Interactable focused = getHoveredInteractable(pos);
        if(focused != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(focused.getWidgetId());
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
            buf.writeDouble(amount);

            ClientPlayNetworking.send(Networking.MOUSE_SCROLLED, buf);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // TODO implement
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // TODO implement
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        //interactables.forEach(interactable -> interactable.onMouseMoved(new Point(mouseX, mouseY)));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return gui.getBounds().isInBounds(new Pos2d(mouseX, mouseY));
    }

    @Deprecated
    @Nullable
    @Override
    public final Element getFocused() { return null; }

    @Deprecated
    @Override
    public final void setFocused(@Nullable Element focused) { }

    public Gui getGui() {
        return gui;
    }

    @Override
    public float getZ() {
        return getZOffset();
    }

    @Override
    public Pos2d getMousePos() {
        return null;
    }

    @Override
    public void setZ(float z) {
        setZOffset((int) z);
    }

    @Override
    public MatrixStack getMatrices() {
        return matrices;
    }

    @Override
    public void setMatrices(MatrixStack matrices) {
        this.matrices = matrices;
    }
}
