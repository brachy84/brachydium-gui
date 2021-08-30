package brachy84.brachydium.gui.api;

import java.util.function.Supplier;

/**
 * A Drawable is basically a {@link TextureArea} supplier
 * and A TextureArea is a IDrawable that returns itself
 */
@FunctionalInterface
public interface ITexture extends Supplier<TextureArea> {

    TextureArea getTexture();

    @Override
    default TextureArea get() {
        return getTexture();
    }
}
