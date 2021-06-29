package brachy84.brachydium.gui.internal;

import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
public interface RenderObject {

    void render(MatrixStack matrices, float delta);
}
