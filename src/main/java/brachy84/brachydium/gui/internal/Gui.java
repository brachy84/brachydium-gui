package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
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

public class Gui {

    public static Size getScreenSize() {
        Window window = MinecraftClient.getInstance().getWindow();
        return new Size(window.getScaledWidth(), window.getScaledHeight());
    }

    private static final Map<Identifier, Gui> GUI_MAP = new HashMap<>();

    public static void registerGui(Identifier id, Gui gui) {
        GUI_MAP.put(id, gui);
    }

    public static Gui get(Identifier id) {
        return GUI_MAP.get(id);
    }

    public final PlayerEntity player;
    private RootWidget root;
    private ISyncedWidget[] syncedWidgets = {};
    private Interactable[] interactables = {};

    public Gui(PlayerEntity player, RootWidget root) {
        this.root = root;
        this.player = player;
    }

    @ApiStatus.Internal
    public void init() {
        root.init(0);

        AtomicInteger syncId = new AtomicInteger();
        List<ISyncedWidget> syncedWidgets = new ArrayList<>();
        root.forAllChildren(widget -> {
            if (widget instanceof ISyncedWidget) {
                syncedWidgets.add((ISyncedWidget) widget);
                ((ISyncedWidget) widget).assignId(syncId.getAndIncrement());
            }
        });
        this.syncedWidgets = syncedWidgets.toArray(new ISyncedWidget[0]);
    }

    public void close() {

    }

    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.draw(matrices, delta);
    }

    public void renderBackground(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.drawBackground(matrices, delta);
    }

    public void onScreenResize() {
        root.forAllChildren(Widget::rePosition);
    }

    @Nullable
    public ISyncedWidget findSyncedWidget(int id) {
        if (id > syncedWidgets.length) {
            throw new IllegalArgumentException(String.format("Can't find synced widget with id %s. Max is %s", id, syncedWidgets.length - 1));
        }
        return syncedWidgets[id];
    }

    public ISyncedWidget[] getSyncedWidgets() {
        return syncedWidgets;
    }

    public List<Widget> getSyncedWidgets(Class<?> clazz) {
        List<Widget> widgets = new ArrayList<>();
        for (ISyncedWidget syncedWidget : syncedWidgets) {
            if (syncedWidget.getClass().isAssignableFrom(clazz)) {
                widgets.add((Widget) syncedWidget);
            }
        }
        return widgets;
    }

    public Interactable[] getInteractables() {
        if (interactables.length == 0) {
            interactables = getSyncedWidgets(Interactable.class).toArray(new Interactable[0]);
        }
        return interactables;
    }

    public Size getGuiSize() {
        return root.getSize();
    }

    public Pos2d getGuiPos() {
        return root.getPos();
    }

    public AABB getBounds() {
        return root.getBounds();
    }
}
