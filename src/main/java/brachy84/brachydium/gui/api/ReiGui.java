package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.internal.Gui;
import me.shedaniel.rei.api.client.gui.widgets.Widget;

import java.util.ArrayList;
import java.util.List;

public class ReiGui {

    /**
     * Returns a list of rei widgets representing the gui inside the bounds
     * @param gui gui to get widgets from
     * @param bounds the bounds of the gui to get widgets from
     * @param reiRecipeBounds the bounds of the recipe display
     * @return rei widgets
     */
    public static List<Widget> getReiWidgetsOf(Gui gui, AABB bounds, AABB reiRecipeBounds) {
        gui.init();
        List<Widget> reiWidgets = new ArrayList<>();
        gui.forEachWidget(widget -> {
            if(bounds.covers(widget.getBounds())) {
                widget.getReiWidgets(reiWidgets, reiRecipeBounds, widget.getPos().subtract(bounds.getTopLeft()));
            }
        });
        gui.close();
        return reiWidgets;
    }
}
