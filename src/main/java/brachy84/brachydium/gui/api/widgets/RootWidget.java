package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.Widget;

public final class RootWidget extends MultiChildWidget {

    public RootWidget(Size size, Alignment alignment) {
        setSize(size);
        setAlignment(alignment);
        setHandleLayout(false);
    }

    public RootWidget(Size size, Pos2d pos) {
        setSize(size);
        setPos(pos);
        setHandleLayout(false);
    }

    @Override
    public void layoutChildren() {
    }

    @Override
    public RootWidget child(Widget child) {
        return (RootWidget) super.child(child);
    }

    @Override
    public RootWidget children(Widget... children) {
        return (RootWidget) super.children(children);
    }

    @Override
    public Widget setPos(Pos2d pos) {
        return super.setPos(pos);
    }
}
