package brachy84.brachydium.gui.internal.old;

import brachy84.brachydium.gui.api.IWidget;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.GuiHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus;

public abstract class WidgetOld implements IWidget, GuiHelper {

    private float z;
    private MatrixStack matrices;
    private boolean isEnabled;
    private Pos2d parentPos;
    private final Pos2d pos;
    private Pos2d absPos;
    private final Size size;

    protected WidgetOld(Pos2d pos, Size size) {
        this.z = 0;
        this.matrices = new MatrixStack();
        this.isEnabled = true;
        this.parentPos = Pos2d.ZERO;
        this.pos = pos;
        this.absPos = pos;
        this.size = size;
    }

    final void drawWidget(MatrixStack matrices, Pos2d mousePos) {
        WidgetOld widget = build();
        render(matrices, mousePos);
    }

    public abstract void render(MatrixStack matrices, Pos2d mousePos);

    public abstract WidgetOld build();

    @FunctionalInterface
    public interface WidgetBuilder {
        WidgetOld build();
    }

    @ApiStatus.Internal
    public void setParentPosition(Pos2d parentPosition) {
        this.parentPos = parentPosition;
        this.absPos = pos.add(parentPosition);
        //transformation.setRotationVector(getBounds().getCenter(), layer);
    }

    @Override
    public float getZ() {
        return z;
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

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public Pos2d getParentPos() {
        return parentPos;
    }

    @Override
    public Pos2d getAbsolutePos() {
        return absPos;
    }

    @Override
    public Pos2d getPos() {
        return pos;
    }

    @Override
    public Size getSize() {
        return size;
    }
}
