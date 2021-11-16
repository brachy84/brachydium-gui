package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.rendering.ITexture;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.rendering.GuiHelper;
import brachy84.brachydium.gui.api.Widget;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A Sprite Widget renders a png file in the gui
 * By default the size is the size of the image
 */
public class SpriteWidget extends Widget {

    private final ITexture drawable;

    public SpriteWidget(ITexture drawable, Size size) {
        this.drawable = drawable;
        setSize(size);
    }

    public SpriteWidget(ITexture drawable, float width, float height) {
        this(drawable, new Size(width, height));
    }

    @Override
    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        GuiHelper.drawTexture(matrices, drawable, getPos(), getSize());
    }

    public ITexture getDrawable() {
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
