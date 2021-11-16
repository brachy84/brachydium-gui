package brachy84.brachydium.gui.api.rendering;

import brachy84.brachydium.gui.api.math.Pos2d;
import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
public interface IDrawable {

    void draw(MatrixStack matrices, Pos2d mousePos, float delta);
}
