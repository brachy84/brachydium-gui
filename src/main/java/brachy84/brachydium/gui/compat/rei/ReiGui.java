package brachy84.brachydium.gui.compat.rei;

import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import com.google.common.util.concurrent.AtomicDouble;
import io.netty.util.internal.UnstableApi;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Converts a {@link Gui} to REI widgets based on {@link brachy84.brachydium.gui.api.Widget#getReiWidgets(AABB, Pos2d)}
 */
@UnstableApi
@Environment(EnvType.CLIENT)
public class ReiGui {

    private final List<Slot> itemSlots = new ArrayList<>();
    private final List<Slot> fluidSlots = new ArrayList<>();
    private final List<Widget> widgets = new ArrayList<>();
    private final AABB bounds;

    public ReiGui(Gui gui, Rectangle recipeBounds, Predicate<brachy84.brachydium.gui.api.Widget> fluidSlotPredicate) {
        this(gui, null, recipeBounds, fluidSlotPredicate);
    }

    /**
     * Returns a list of rei widgets representing the gui inside the bounds
     *
     * @param gui                gui to get widgets from
     * @param bounds             the bounds of the gui to get widgets from (by default the whole gui)
     * @param recipeBounds       the bounds of the recipe display
     * @param fluidSlotPredicate returns if the widget is a fluid slot
     */
    public ReiGui(Gui gui, @Nullable AABB bounds, Rectangle recipeBounds, Predicate<brachy84.brachydium.gui.api.Widget> fluidSlotPredicate) {
        final AtomicDouble x0 = new AtomicDouble(0), x1 = new AtomicDouble(0), y0 = new AtomicDouble(0), y1 = new AtomicDouble(0);
        gui.init();
        AABB finalBounds = bounds == null ? gui.getBounds() : bounds;
        gui.forEachWidget(widget -> {
            AABB widgetBounds = widget.getBounds();
            if (finalBounds.covers(widgetBounds)) {
                Pos2d transform = widget.getParent().getPos().add(-recipeBounds.x, -recipeBounds.y);//widget.getPos().subtract(new Pos2d(recipeBounds.x, recipeBounds.y));
                float x = transform.x, y = transform.y;
                x += 20;
                y += 2;
                transform = new Pos2d(x, y);
                List<Widget> widgets = widget.getReiWidgets(finalBounds, widget.getPos().subtract(transform)/*.subtract(new Pos2d(recipeBounds.x, recipeBounds.y))*/);
                if (widget instanceof ItemSlotWidget) {
                    for (Widget widget1 : widgets) {
                        if (widget1 instanceof Slot)
                            itemSlots.add((Slot) widget1);
                    }
                } else if (fluidSlotPredicate.test(widget)) {
                    for (Widget widget1 : widgets) {
                        if (widget1 instanceof Slot)
                            fluidSlots.add((Slot) widget1);
                    }
                }
                this.widgets.addAll(widgets);
                if (x0.floatValue() == 0 || x0.floatValue() > widgetBounds.x0) {
                    x0.set(widgetBounds.x0);
                }
                if (x1.floatValue() == 0 || x1.floatValue() < widgetBounds.x1) {
                    x1.set(widgetBounds.x1);
                }
                if (y0.floatValue() == 0 || y0.floatValue() > widgetBounds.y0) {
                    y0.set(widgetBounds.y0);
                }
                if (y1.floatValue() == 0 || y1.floatValue() < widgetBounds.y1) {
                    y1.set(widgetBounds.y1);
                }
            }
        });
        gui.close();
        this.bounds = AABB.ltwh(x0.floatValue(), y0.floatValue(), x1.floatValue() - x0.floatValue(), y1.floatValue() - y0.floatValue());
    }

    public List<Slot> getItemSlots() {
        return itemSlots;
    }

    public List<Slot> getFluidSlots() {
        return fluidSlots;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public AABB getBounds() {
        return bounds;
    }
}
