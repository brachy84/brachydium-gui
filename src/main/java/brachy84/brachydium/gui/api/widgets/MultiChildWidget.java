package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.internal.Widget;

public interface MultiChildWidget {

    /**
     * adds the child to the widget
     * @param child to add
     * @return the widget so it can be used in a builder
     */
    Widget child(Widget child);

    /**
     * Adds children to the widget
     * @param children to add
     * @return the widget so it can be used in a builder
     */
    Widget children(Widget... children);
}
