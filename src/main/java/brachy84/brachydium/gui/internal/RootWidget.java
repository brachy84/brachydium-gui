package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;

public final class RootWidget extends Widget {

    public RootWidget(Size size, Pos2d pos) {
        super(size, pos);
    }
    public RootWidget(AABB bounds) {
        super(bounds);
    }

    protected void init(int layer) {
        init(DUMMY_PARENT, layer);
    }

    private static final Widget DUMMY_PARENT = new Widget() {
    };
}
