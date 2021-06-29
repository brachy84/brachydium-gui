package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.internal.Widget;
import org.jetbrains.annotations.Nullable;

public interface SingleChildWidget{

    /**
     * @return the child of the widget, null if it doesn't have a child
     */
    @Nullable
    Widget getChild();

    /**
     * Sets the child of the widget
     * This should only be set once
     * @param child of the widget
     * @return the widget so it can be used in a builder
     */
    Widget child(Widget child);

    /**
     * If this returns true and it doesn't have a child at init
     * a {@link IllegalStateException} will be thrown
     * @throws IllegalStateException when widget does not have a child
     * @return if the widget must have a child
     */
    default boolean mustHaveChild() {
        return false;
    }
}
