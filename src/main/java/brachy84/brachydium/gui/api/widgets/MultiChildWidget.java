package brachy84.brachydium.gui.api.widgets;

import org.jetbrains.annotations.ApiStatus;

public abstract class MultiChildWidget extends Widget {

    private boolean doHandleLayout;

    public MultiChildWidget() {
        this.doHandleLayout = true;
    }

    @Override
    public void onInit() {
        if(doHandleLayout) {
            layoutChildren();
        }
    }

    @ApiStatus.Internal
    public abstract void layoutChildren();

    /**
     * adds the child to the widget
     *
     * @param child to add
     * @return the widget so it can be used in a builder
     */
    public MultiChildWidget child(Widget child) {
        addChild(child);
        return this;
    }

    /**
     * Adds children to the widget
     *
     * @param children to add
     * @return this
     */
    public MultiChildWidget children(Widget... children) {
        for(Widget child : children)
            addChild(child);
        return this;
    }

    /**
     * Set if the children position should be handled by this widget
     * @param doHandle shouldHandleLayout
     * @return this
     */
    protected MultiChildWidget setHandleLayout(boolean doHandle) {
        this.doHandleLayout = doHandle;
        return this;
    }

    public boolean doesHandleLayout() {
        return doHandleLayout;
    }
}
