package brachy84.brachydium.gui.api;

import java.util.function.Supplier;

@FunctionalInterface
public interface IDrawable extends Supplier<TextureArea> {

    TextureArea getTexture();

    @Override
    default TextureArea get() {
        return getTexture();
    }
}
