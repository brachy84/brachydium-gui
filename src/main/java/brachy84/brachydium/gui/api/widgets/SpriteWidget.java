package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.RenderObject;
import brachy84.brachydium.gui.internal.Widget;
import org.jetbrains.annotations.Nullable;

public class SpriteWidget extends Widget {

    private TextureArea sprite;

    public SpriteWidget(TextureArea sprite) {
        super(sprite.getImageSize(), Pos2d.ZERO);
        this.sprite = sprite;
    }

    @Override
    public @Nullable RenderObject getRenderObject() {
        return ((matrices, delta) -> guiHelper.drawTexture(matrices, sprite, getPos(), getSize()));
    }

    public TextureArea getSprite() {
        return sprite;
    }

    public SpriteWidget setSprite(TextureArea sprite) {
        if(this.sprite != null && this.sprite.getImageSize().equals(getSize())) {
            setSize(sprite.getImageSize());
        }
        this.sprite = sprite;
        return this;
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
