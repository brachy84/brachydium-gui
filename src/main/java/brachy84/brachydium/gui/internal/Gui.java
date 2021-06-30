package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Gui {

    public static Size getScreenSize() {
        Window window = MinecraftClient.getInstance().getWindow();
        return new Size(window.getScaledWidth(), window.getScaledHeight());
    }

    private static final Widget DUMMY_PARENT = new Widget() {
    };

    private static final Map<Identifier, Gui> GUI_MAP = new HashMap<>();

    public static void registerGui(Identifier id, Gui gui) {
        GUI_MAP.put(id, gui);
    }

    public static Gui get(Identifier id) {
        return GUI_MAP.get(id);
    }

    public final PlayerEntity player;
    private final RootWidget root;
    private GuiScreen screen;
    private final CursorSlotWidget cursorSlot;
    private Interactable[] interactables = {};
    private Int2ObjectMap<ISyncedWidget> SYNCED_WIDGET_MAP;
    private Object2IntMap<ISyncedWidget> SYNCED_ID_MAP;

    public Gui(PlayerEntity player, RootWidget root) {
        this.root = root;
        this.player = player;
        cursorSlot = new CursorSlotWidget();
    }

    @ApiStatus.Internal
    public void init() {
        root.init(this, DUMMY_PARENT, 0);
        cursorSlot.init(this, DUMMY_PARENT, 1000);
        AtomicInteger syncId = new AtomicInteger();
        BiMap<Integer, ISyncedWidget> syncedWidgets = HashBiMap.create(0);
        syncedWidgets.forcePut(syncId.getAndIncrement(), cursorSlot);
        root.forAllChildren(widget -> {
            if (widget instanceof ISyncedWidget) {
                syncedWidgets.forcePut(syncId.getAndIncrement(), (ISyncedWidget) widget);
            }
        });
        SYNCED_WIDGET_MAP = new Int2ObjectArrayMap<>(syncedWidgets);
        SYNCED_ID_MAP = new Object2IntArrayMap<>(syncedWidgets.inverse());
        SYNCED_ID_MAP.defaultReturnValue(Integer.MIN_VALUE);
    }

    public void close() {
        cursorSlot.onDestroy();
        root.forAllChildren(Widget::onDestroy);
    }

    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.draw(matrices, mousePos, delta);
    }

    public void renderForeground(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.drawForeground(matrices, delta);
    }

    public void onScreenResize() {
        root.forAllChildren(Widget::rePosition);
    }

    public ISyncedWidget findSyncedWidget(int id) {
        ISyncedWidget syncedWidget = SYNCED_WIDGET_MAP.get(id);
        if (syncedWidget == null)
            throw new IllegalArgumentException(String.format("Can't find synced widget with id %s.", id));
        return syncedWidget;
    }

    public int findIdForSyncedWidget(ISyncedWidget widget) {
        int id = SYNCED_ID_MAP.getInt(Objects.requireNonNull(widget));
        if (id == Integer.MIN_VALUE)
            throw new IllegalArgumentException("Can't find id for SyncedWidget");
        return id;
    }

    public Collection<ISyncedWidget> getSyncedWidgets() {
        return Collections.unmodifiableCollection(SYNCED_WIDGET_MAP.values());
    }

    public Interactable[] getInteractables() {
        if (interactables.length == 0) {
            interactables = getMatchingSyncedWidgets(widget -> widget instanceof Interactable).toArray(Interactable[]::new);
        }
        return interactables;
    }

    public List<Widget> getMatchingWidgets(Predicate<Widget> predicate) {
        List<Widget> widgets = new ArrayList<>();
        root.forAllChildren(widget -> {
            if(predicate.test(widget)) {
                widgets.add(widget);
            }
        });
        return widgets;
    }

    public <T extends Widget & ISyncedWidget> List<T> getMatchingSyncedWidgets(Predicate<T> predicate) {
        List<T> widgets = new ArrayList<>();
        for(ISyncedWidget syncedWidget : getSyncedWidgets()) {
            if(predicate.test((T) syncedWidget)) {
                widgets.add((T) syncedWidget);
            }
        }
        return widgets;
    }


    public CursorSlotWidget getCursorSlot() {
        return cursorSlot;
    }

    public ItemStack getCursorStack() {
        return getCursorSlot().getResource();
    }

    public void setCursorStack(ItemStack stack) {
        getCursorSlot().setResource(stack);
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

    public GuiScreen getScreen() {
        return screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }
}
