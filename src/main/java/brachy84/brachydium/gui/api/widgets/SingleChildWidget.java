package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.internal.Widget;
import org.jetbrains.annotations.Nullable;

public abstract class SingleChildWidget extends Widget {

    /**
     * @return the child of the widget, null if it doesn't have a child
     * if {@link #mustHaveChild()} returns true then this will never bw null
     */
    public @Nullable Widget getChild() {
        return hasChildren() ? getChildren().get(0) : null;
    }

    /**
     * Sets the child of the widget
     * This should only be set once
     *
     * @param child of the widget
     * @return the widget so it can be used in a builder
     */
    public Widget setChild(Widget child) {
        setChildInternal(child);
        return this;
    }

    /**
     * If this returns true and it doesn't have a child at init
     * a {@link IllegalStateException} will be thrown
     *
     * @return if the widget must have a child
     * @throws IllegalStateException when widget does not have a child
     */
    public boolean mustHaveChild() {
        return false;
    }
}
