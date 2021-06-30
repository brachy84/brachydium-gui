package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.MultiChildWidget;
import brachy84.brachydium.gui.api.widgets.SingleChildWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This is the base of all widgets.
 * A widget is an Element in a Gui. It does not necessarily draw anything
 * onto the screen. It might just help organizing layouts and children
 *
 * @see SingleChildWidget
 * @see MultiChildWidget
 */
public abstract class Widget {

    private Widget parent;
    private final List<Widget> children = new ArrayList<>();
    private int layer;
    private Pos2d pos;
    private Pos2d relativePos;
    private Size size;
    private Alignment alignment;
    private boolean initialised;
    private boolean enabled;

    public Widget() {
        this(Size.ZERO, Pos2d.ZERO);
    }

    public Widget(AABB bounds) {
        this(bounds.getSize(), bounds.getTopLeft());
    }

    public Widget(Size size, Pos2d pos) {
        this.relativePos = Objects.requireNonNull(pos);
        this.pos = Pos2d.ZERO;
        this.size = Objects.requireNonNull(size);
        this.layer = 0;
        this.alignment = Alignment.TopLeft;
        this.initialised = false;
        this.enabled = true;
    }

    @ApiStatus.Internal
    public final void init(Widget parent, int layer) {
        if (this.parent != null)
            throw new IllegalStateException("Init should only be called once from Gui");
        this.parent = Objects.requireNonNull(parent);
        this.layer = layer;
        if (!relativePos.isZero()) {
            this.pos = parent.pos.add(relativePos);
        }
        setPos(alignment.getAlignedPos(parent.size, size));
        if (this instanceof SingleChildWidget widget && widget.mustHaveChild() && !hasChildren())
            throw new IllegalStateException("Widget is marked as 'mustHaveChild', but doesn't have a child");
        onInit();
        this.initialised = true;
        for (Widget widget : children) {
            widget.init(this, layer + 1);
        }
    }

    @ApiStatus.Internal
    public final void drawBackground(MatrixStack matrices, float delta) {
        if(!isEnabled()) return;
        RenderObject renderObject = getBackgroundRenderObject();
        if (renderObject != null) {
            matrices.push();
            matrices.translate(0, 0, layer);
            renderObject.render(matrices, delta);
            matrices.pop();
        }
        for (Widget widget : children) {
            widget.drawBackground(matrices, delta);
        }
    }

    @ApiStatus.Internal
    public final void draw(MatrixStack matrices, float delta) {
        if(!isEnabled()) return;
        RenderObject renderObject = getRenderObject();
        if (renderObject != null) {
            matrices.push();
            matrices.translate(0, 0, layer);
            renderObject.render(matrices, delta);
            matrices.pop();
        }
        for (Widget widget : children) {
            widget.draw(matrices, delta);
        }
    }

    public void rePosition() {
        setPos(alignment.getAlignedPos(parent.size, size));
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * applies an operation to this widget, it's children and all it's sub-children
     *
     * @param consumer operation to apply
     */
    public final void forAllChildren(Consumer<Widget> consumer) {
        consumer.accept(this);
        for (Widget widget : children) {
            widget.forAllChildren(consumer);
        }
    }

    public void onInit() {
    }

    @Nullable
    public RenderObject getRenderObject() {
        return null;
    }

    @Nullable
    public RenderObject getBackgroundRenderObject() {
        return null;
    }

    public List<Widget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    protected void addChild(Widget widget) {
        if (this instanceof SingleChildWidget && children.size() > 0)
            throw new IllegalStateException("SingleChildWidget can only hold a single widget");
        this.children.add(Objects.requireNonNull(widget));
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

    public Alignment getAlignment() {
        return alignment;
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected Widget setPos(Pos2d pos) {
        this.relativePos = Objects.requireNonNull(pos);
        if (initialised) {
            forAllChildren(widget -> {
                widget.pos = widget.parent.pos.add(widget.relativePos);
            });
        }
        return this;
    }

    protected Widget setSize(Size size) {
        this.size = Objects.requireNonNull(size);
        if (initialised) {
            forAllChildren(Widget::rePosition);
        }
        return this;
    }

    protected Widget setAlignment(Alignment alignment) {
        this.alignment = Objects.requireNonNull(alignment);
        if (initialised) {
            forAllChildren(Widget::rePosition);
        }
        return this;
    }

}
