package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.widgets.MultiChildWidget;
import net.minecraft.entity.player.PlayerEntity;

public final class RootWidget extends Widget implements MultiChildWidget {

    public RootWidget(Size size, Alignment alignment) {
        super(size, Pos2d.ZERO);
        setAlignment(alignment);
    }

    public RootWidget(Size size, Pos2d pos) {
        super(size, pos);
    }
    public RootWidget(AABB bounds) {
        super(bounds);
    }

    public Gui createGui(PlayerEntity player) {
        return new Gui(player, this);
    }

    @Override
    public void rePosition() {
        setPos(getAlignment().getAlignedPos(Gui.getScreenSize(), getSize()));
    }

    @Override
    public RootWidget child(Widget child) {
        addChild(child);
        return this;
    }

    @Override
    public RootWidget children(Widget... children) {
        for(Widget widget : children) {
            addChild(widget);
        }
        return this;
    }
}
