package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.Draggable;
import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.WidgetTag;
import brachy84.brachydium.gui.api.math.*;
import brachy84.brachydium.gui.api.widgets.MultiChildWidget;
import brachy84.brachydium.gui.api.widgets.RootWidget;
import brachy84.brachydium.gui.api.widgets.SingleChildWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The base of all Widgets
 */
public abstract class Widget {

    private static final Logger LOG = LogManager.getLogger("BrachydiumGui");

    private Gui gui;
    private Widget parent;
    private final List<Widget> children = new ArrayList<>();
    private final List<WidgetTag> tags = new ArrayList<>();
    private int layer;
    private Pos2d pos;
    private Pos2d relativePos;
    private Size size;
    @Nullable
    private Alignment alignment;
    private EdgeInset edgeInset;
    private boolean initialised;
    private boolean enabled;

    public Widget() {
        this.relativePos = Pos2d.ZERO;
        this.pos = Pos2d.ZERO;
        this.size = Size.ZERO;
        this.layer = 0;
        this.alignment = null;
        this.edgeInset = EdgeInset.ZERO;
        this.initialised = false;
        this.enabled = true;
    }

    @ApiStatus.Internal
    public final void init(Gui gui, Widget parent, int layer) {
        if (this.parent != null)
            throw new IllegalStateException("Init should only be called once from Gui");
        if (this instanceof SingleChildWidget widget && widget.mustHaveChild() && !hasChildren())
            throw new IllegalStateException("Widget is marked as 'mustHaveChild', but doesn't have a child");
        if(this instanceof Draggable)
            ((Draggable) this).setState(Draggable.State.IDLE);
        validateSize();
        this.gui = gui;
        this.parent = Objects.requireNonNull(parent);
        this.layer = layer;
        rePosition();
        onInit();
        this.initialised = true;
        for (Widget widgetOld : children) {
            widgetOld.init(gui, this, layer + 10);
        }
    }

    @ApiStatus.Internal
    public final void drawWidget(MatrixStack matrices, float delta, Pos2d mousePos, boolean foreground) {
        if(!isEnabled()) return;
        if(!size.isZero()) {
            matrices.push();
            matrices.translate(0, 0, layer);
            if (foreground)
                renderForeground(GuiHelper.create(layer, mousePos), matrices, delta);
            else
                render(GuiHelper.create(layer, mousePos), matrices, delta);
            matrices.pop();
        }
        children.forEach(widget -> widget.drawWidget(matrices, delta, mousePos, foreground));
    }

    @ApiStatus.OverrideOnly
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
    }

    @ApiStatus.OverrideOnly
    public void renderForeground(IGuiHelper helper, MatrixStack matrices, float delta) {
    }

    public void validateSize() {
        if (!initialised) return;
        if (size.height() > parent.size.height() || size.width() > parent.size.width())
            throw new IllegalStateException("Child size can't be larger than Parent size");
    }

    @ApiStatus.Internal
    public void rePosition() {
        if (!initialised) return;
        if (alignment != null) {
            if (edgeInset.isZero())
                this.relativePos = alignment.getAlignedPos(parent.size, size);
            else
                this.relativePos = alignment.getAlignedPos(parent.size, size, edgeInset);
        }
        this.pos = parent.pos.add(relativePos);
        if (this instanceof MultiChildWidget widget && widget.doesHandleLayout())
            widget.layoutChildren();
    }

    public void recalculateLayout() {
        forAllChildren(Widget::rePosition);
    }


    /**
     * applies an operation to this widget, it's children and all it's sub-children
     *
     * @param consumer operation to apply
     */
    public final void forAllChildren(Consumer<Widget> consumer) {
        consumer.accept(this);
        for (Widget widgetOld : children) {
            widgetOld.forAllChildren(consumer);
        }
    }

    @ApiStatus.OverrideOnly
    public void onInit() {
    }

    @ApiStatus.OverrideOnly
    public void onDestroy() {
    }

    //=================================================
    //  Builder methods

    /**
     * This is the only way to add widgets outside if this class
     *
     * @param widget to add
     * @throws IllegalArgumentException if the widget is a {@link RootWidget} or a {@link CursorWidget}
     * @throws IllegalStateException    if this is a {@link SingleChildWidget} and it already has a child
     * @throws IllegalStateException    if the widget is already initialised
     */
    protected final void addChild(Widget widget) {
        if (initialised)
            throw new IllegalStateException("Can't add children after initialised");
        if (widget instanceof CursorWidget || widget instanceof RootWidget)
            throw new IllegalArgumentException("CursorSlot or RootWidgets can't be added");
        if (this instanceof SingleChildWidget && children.size() > 0)
            throw new IllegalStateException("SingleChildWidget can only hold a single widget");
        this.children.add(Objects.requireNonNull(widget));
    }

    protected final void setChildInternal(Widget widget) {
        if (initialised)
            throw new IllegalStateException("Can't add children after initialised");
        if (widget instanceof CursorWidget || widget instanceof RootWidget)
            throw new IllegalArgumentException("CursorSlot or RootWidgets can't be added");
        if(hasChildren())
            this.children.set(0, Objects.requireNonNull(widget));
        else
            this.children.add(Objects.requireNonNull(widget));
    }

    public Widget addTag(WidgetTag tag) {
        for (WidgetTag tag1 : tags) {
            if (!tag.getCompatPredicate().test(tag1)) {
                throw new IllegalArgumentException("WidgetTags are incompatible");
            }
        }
        tags.add(tag);
        return this;
    }

    public Widget setSize(Size size) {
        this.size = Objects.requireNonNull(size);
        validateSize();
        return this;
    }

    public Widget setPos(Pos2d pos) {
        this.relativePos = Objects.requireNonNull(pos);
        this.alignment = null;
        if (initialised)
            this.pos = parent.pos.add(relativePos);
        return this;
    }

    public Widget setAbsolutePos(Pos2d pos) {
        this.pos = Objects.requireNonNull(pos);
        this.alignment = null;
        if (initialised)
            this.relativePos = this.pos.subtract(parent.pos);
        return this;
    }

    /**
     * This will align this widget inside it's parent
     *
     * @param alignment alignment
     * @return this
     */
    public Widget setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Defines the distance to the widgets parent
     * Is only applied when {@link #getAlignment()} is not null
     *
     * @param edgeInset edgeInset
     * @return this
     */
    public Widget setMargin(EdgeInset edgeInset) {
        this.edgeInset = edgeInset == null ? EdgeInset.ZERO : edgeInset;
        return this;
    }

    /**
     * removes the alignment
     *
     * @return this
     */
    public Widget unAlign() {
        this.alignment = null;
        return this;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //=================================================
    //  Getter

    public boolean hasTag(WidgetTag tag) {
        return tags.contains(tag);
    }

    public List<Widget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public Widget getParent() {
        return parent;
    }

    public Pos2d getPos() {
        return pos;
    }

    public Pos2d getRelativePos() {
        return relativePos;
    }

    public Size getSize() {
        return size;
    }

    public AABB getBounds() {
        return AABB.of(getSize(), getPos());
    }

    @Nullable
    public Alignment getAlignment() {
        return alignment;
    }

    public EdgeInset getMargin() {
        return edgeInset;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Gui getGui() {
        return gui;
    }

    public boolean isInBounds(Pos2d pos) {
        return getBounds().isInBounds(pos);
    }

    /**
     * Add Rei widgets to the list which represent together this widget
     * @param widgets list of rei widgets that represent this widget
     */
    public void getReiWidgets(List<me.shedaniel.rei.api.client.gui.widgets.Widget> widgets) {
    }
}
