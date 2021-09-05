package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.internal.Gui;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReiGui {

    private final List<Slot> itemSlots = new ArrayList<>();
    private final List<Slot> fluidSlots = new ArrayList<>();
    private final List<Widget> widgets = new ArrayList<>();

    public ReiGui(Gui gui, Rectangle recipeBounds, Predicate<brachy84.brachydium.gui.internal.Widget> fluidSlotPredicate) {
        this(gui, gui.getBounds(), recipeBounds, fluidSlotPredicate);
    }

    /**
     * Returns a list of rei widgets representing the gui inside the bounds
     *
     * @param gui                gui to get widgets from
     * @param bounds             the bounds of the gui to get widgets from
     * @param recipeBounds       the bounds of the recipe display
     * @param fluidSlotPredicate returns if the widget is a fluid slot
     */
    public ReiGui(Gui gui, AABB bounds, Rectangle recipeBounds, Predicate<brachy84.brachydium.gui.internal.Widget> fluidSlotPredicate) {
        gui.init();
        gui.forEachWidget(widget -> {
            if (bounds.covers(widget.getBounds())) {
                List<Widget> widgets = widget.getReiWidgets(bounds, widget.getPos().subtract(bounds.getTopLeft()));
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
            }
        });
        gui.close();
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
}
