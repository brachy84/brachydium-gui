package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.IDrawable;
import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A Sprite Widget renders a png file in the gui
 * By default the size is the size of the image
 */
public class SpriteWidget extends Widget {

    private final IDrawable drawable;

    public SpriteWidget(IDrawable drawable, Size size) {
        this.drawable = drawable;
        setSize(size);
    }

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        helper.drawTexture(matrices, drawable, getPos(), getSize());
    }

    public IDrawable getDrawable() {
        return drawable;
    }

    @Override
    public SpriteWidget setSize(Size size) {
        super.setSize(size);
        return this;
    }

    @Override
    public SpriteWidget setAlignment(Alignment alignment) {
        super.setAlignment(alignment);
        return this;
    }

    @Override
    public SpriteWidget setPos(Pos2d pos) {
        super.setPos(pos);
        return this;
    }
}
