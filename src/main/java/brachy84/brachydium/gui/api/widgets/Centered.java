package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.Widget;

public class Centered extends Widget implements SingleChildWidget {

    public Centered(Size size) {
        super(size, Pos2d.ZERO);
        setAlignment(Alignment.Center);
    }

    @Override
    public Widget setSize(Size size) {
        return super.setSize(size);
    }

    @Override
    public Widget getChild() {
        if(!hasChildren()) return null;
        return getChildren().get(0);
    }

    @Override
    public Centered child(Widget widget) {
        addChild(widget);
        return this;
    }
}
