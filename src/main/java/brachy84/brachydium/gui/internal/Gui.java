package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Gui {

    private static final Map<Identifier, Gui> GUI_MAP = new HashMap<>();

    public static void registerGui(Identifier id, Gui gui) {
        GUI_MAP.put(id, gui);
    }

    public static Gui get(Identifier id) {
        return GUI_MAP.get(id);
    }

    public final PlayerEntity player;
    private Size screenSize;
    private Widget root;
    private ISyncedWidget[] syncedWidgets = {};
    private Interactable[] interactables = {};

    public Gui(PlayerEntity player, Widget root) {
        this.root = root;
        this.player = player;
    }

    @ApiStatus.Internal
    public void init() {
        root.setLayer(0);
        setLayers(0, root);
        forEachWidget(root, widget -> widget.onInit(widget.getParent()));
        AtomicInteger syncId = new AtomicInteger();
        List<ISyncedWidget> syncedWidgets = new ArrayList<>();
        forEachWidget(root, widget -> {
            if(widget instanceof ISyncedWidget) {
                syncedWidgets.add((ISyncedWidget) widget);
                ((ISyncedWidget) widget).assignId(syncId.getAndIncrement());
            }
        });
        this.syncedWidgets = syncedWidgets.toArray(new ISyncedWidget[0]);
    }

    public void close() {

    }

    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.drawWidget(matrices, mousePos, delta, false);
    }

    public void renderForeground(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.drawWidget(matrices, mousePos, delta, true);
    }

    public void resize(Size size) {
        this.screenSize = size;
        root.onScreenResize();
    }

    public void forEachWidget(Widget widget, Consumer<Widget> consumer) {
        consumer.accept(widget);
        widget.forEachChild(widget1 -> forEachWidget(widget1, consumer));
    }

    public void setLayers(int layer, Widget widget) {
        layer += 1;
        widget.setLayer(layer);
        int finalLayer = layer;
        widget.forEachChild(widget1 -> setLayers(finalLayer, widget1));
    }

    @Nullable
    public ISyncedWidget findSyncedWidget(int id) {
        if(id > syncedWidgets.length) {
            throw new IllegalArgumentException(String.format("Can't find synced widget with id %s. Max is %s", id, syncedWidgets.length - 1));
        }
        return syncedWidgets[id];
    }

    public ISyncedWidget[] getSyncedWidgets() {
        return syncedWidgets;
    }

    public List<Widget> getSyncedWidgets(Class<?> clazz) {
        List<Widget> widgets = new ArrayList<>();
        for(ISyncedWidget syncedWidget : syncedWidgets) {
            if(syncedWidget.getClass().isAssignableFrom(clazz)) {
                widgets.add((Widget) syncedWidget);
            }
        }
        return widgets;
    }

    public Interactable[] getInteractables() {
        if(interactables.length == 0) {
            interactables = getSyncedWidgets(Interactable.class).toArray(new Interactable[0]);
        }
        return interactables;
    }

    public Size getScreenSize() {
        return screenSize;
    }

    public Size getGuiSize() {
        return root.getSize();
    }

    public Pos2d getGuiPos() {
        return root.getAbsolutePos();
    }

    public AABB getBounds() {
        return root.getBounds();
    }
}
