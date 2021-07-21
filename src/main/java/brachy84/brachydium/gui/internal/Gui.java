package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.IDrawable;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.*;
import brachy84.brachydium.gui.api.widgets.Layout.CrossAxisAlignment;
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

public final class Gui {

    private static final ScreenWidget SCREEN_WIDGET = new ScreenWidget();

    private static class ScreenWidget extends Widget {
        @Override
        public Widget setSize(Size size) {
            return super.setSize(size);
        }
    }

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

    protected Gui(PlayerEntity player, RootWidget root) {
        this.root = root;
        this.player = player;
        cursorSlot = new CursorSlotWidget();
    }

    public static Builder builder(PlayerEntity player, IDrawable background) {
        return new Builder(player, new SpriteWidget(background));
    }

    public static Builder builder(PlayerEntity player, Size size) {
        return new Builder(player, size);
    }

    @ApiStatus.Internal
    public void init() {
        System.out.println("Init GUI");
        SCREEN_WIDGET.setSize(getScreenSize());
        root.init(this, SCREEN_WIDGET, 0);
        cursorSlot.init(this, SCREEN_WIDGET, 100);
        AtomicInteger syncId = new AtomicInteger();
        BiMap<Integer, ISyncedWidget> syncedWidgets = HashBiMap.create(0);
        syncedWidgets.forcePut(syncId.getAndIncrement(), cursorSlot);
        root.forAllChildren(widget -> {
            if (widget instanceof ISyncedWidget) {
                syncedWidgets.forcePut(syncId.getAndIncrement(), (ISyncedWidget) widget);
            }
        });
        System.out.println("Setting synced maps");
        SYNCED_WIDGET_MAP = new Int2ObjectArrayMap<>(syncedWidgets);
        SYNCED_ID_MAP = new Object2IntArrayMap<>(syncedWidgets.inverse());
        SYNCED_ID_MAP.defaultReturnValue(Integer.MIN_VALUE);
    }

    public void recalculateLayout() {
        root.forAllChildren(Widget::rePosition);
    }

    public void close() {
        cursorSlot.onDestroy();
        root.forAllChildren(Widget::onDestroy);
    }

    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        root.drawWidget(matrices, delta, mousePos, false);
        root.drawWidget(matrices, delta, mousePos, true);
        cursorSlot.drawWidget(matrices, delta, mousePos, false);
    }

    public void onScreenResize() {
        SCREEN_WIDGET.setSize(getScreenSize());
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
        List<Widget> widgetOlds = new ArrayList<>();
        root.forAllChildren(widget -> {
            if (predicate.test(widget)) {
                widgetOlds.add(widget);
            }
        });
        return widgetOlds;
    }

    public <T extends Widget & ISyncedWidget> List<T> getMatchingSyncedWidgets(Predicate<T> predicate) {
        List<T> widgets = new ArrayList<>();
        for (ISyncedWidget syncedWidget : getSyncedWidgets()) {
            if (predicate.test((T) syncedWidget)) {
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

    public static Size getScreenSize() {
        Window window = MinecraftClient.getInstance().getWindow();
        return new Size(window.getScaledWidth(), window.getScaledHeight());
    }

    public static class Builder {
        private final PlayerEntity player;
        private SpriteWidget background;
        private final Size size;
        private Alignment alignment;
        private Pos2d pos;
        private final List<Widget> children = new ArrayList<>();

        private Builder(PlayerEntity player, SpriteWidget background) {
            this.player = player;
            this.background = background;
            this.size = background.getSize();
            this.alignment = Alignment.Center;
        }

        private Builder(PlayerEntity player, Size size) {
            this.player = player;
            this.size = size;
            this.alignment = Alignment.Center;
        }

        public Builder setAlignment(Alignment alignment) {
            this.alignment = alignment;
            this.pos = null;
            return this;
        }

        public Builder setPos(Pos2d pos) {
            this.pos = pos;
            this.alignment = null;
            return this;
        }

        public Builder child(Widget widget) {
            children.add(widget);
            return this;
        }

        public Builder children(Widget... widgets) {
            Collections.addAll(children, widgets);
            return this;
        }

        public Builder bindPlayerInventory(float padding, Alignment alignment) {
            float x = 0, y = 0;
            if(alignment.x() == -1) x += padding;
            else if(alignment.x() == 1) x -= padding;
            if(alignment.y() == -1) y += padding;
            else if(alignment.y() == 1) y -= padding;
            return bindPlayerInventory(alignment.getAlignedPos(size, new Size(9 * 18, 4 * 18 + 5)).add(x, y));
        }

        public Builder bindPlayerInventory(float y, CrossAxisAlignment horizontalAlignment) {
            float x = size.width() * ((horizontalAlignment.value + 1) / 2) - 9 * 9;
            return bindPlayerInventory(new Pos2d(x, y));
        }

        public Builder bindPlayerInventory(Pos2d pos) {
            for(int i = 0; i < 3; ++i) {
                for(int j = 0; j < 9; ++j) {
                    child(new ItemSlotWidget(player.getInventory(), j + i * 9 + 9, pos));
                    pos = pos.add(18, 0);
                }
                pos = pos.add(-18 * 9, 18);
            }
            return bindHotbar(pos.add(0, 5));
        }

        public Builder bindHotbar(float y, CrossAxisAlignment horizontalAlignment) {
            float x = size.width() * ((horizontalAlignment.value + 1) / 2);
            return bindHotbar(new Pos2d(x, y));
        }

        public Builder bindHotbar(Pos2d pos) {
            for(int i = 0; i < 9; i++) {
                child(new ItemSlotWidget(player.getInventory(), i, pos));
                pos = pos.add(18, 0);
            }
            return this;
        }

        public Gui build() {
            RootWidget root = alignment == null ? new RootWidget(size, pos) : new RootWidget(size, alignment);
            if(background != null)
                root.child(background);
            root.children(children.toArray(new Widget[0]));
            return new Gui(player, root);
        }

    }
}
