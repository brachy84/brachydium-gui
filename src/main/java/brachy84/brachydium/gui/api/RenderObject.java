package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.internal.GuiHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class RenderObject implements GuiHelper {

    private GuiRenderer renderer;
    private float z;
    private MatrixStack matrices;
    private Pos2d mousePos;

    public RenderObject(GuiRenderer renderer) {
        this.renderer = renderer;
        setZ(0);
        setMatrices(new MatrixStack());
    }

    public void render(MatrixStack matrices, Pos2d mousePos, AABB bounds, float delta) {
        setMatrices(matrices);
        this.mousePos = mousePos;
        renderer.render(this, bounds, delta);
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public Pos2d getMousePos() {
        return mousePos;
    }

    @Override
    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public MatrixStack getMatrices() {
        return matrices;
    }

    @Override
    public void setMatrices(MatrixStack matrices) {
        this.matrices = matrices;
    }

    @FunctionalInterface
    public interface GuiRenderer {

        void render(IGuiHelper guiHelper, AABB bounds, float delta);
    }
}
