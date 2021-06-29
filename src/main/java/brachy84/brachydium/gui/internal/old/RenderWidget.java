package brachy84.brachydium.gui.internal.old;

import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.RenderObject;
import brachy84.brachydium.gui.api.math.AABB;
import org.jetbrains.annotations.Nullable;

public abstract class RenderWidget<T extends Widget<?>> extends Widget<T>{

    private final RenderObject renderer;
    private final RenderObject foregroundRenderer;

    protected RenderWidget() {
        this.renderer = new RenderObject(this::render);
        this.foregroundRenderer = new RenderObject(this::renderForeground);
    }

    public abstract void render(IGuiHelper guiHelper, AABB bounds, float delta);

    public abstract void renderForeground(IGuiHelper guiHelper, AABB bounds, float delta);

    @Override
    public boolean isRenderable() {
        return true;
    }

    @Nullable
    @Override
    public RenderObject getRenderer(boolean isForeground) {
        return isForeground ? foregroundRenderer : renderer;
    }

    public RenderObject getRenderer() {
        return renderer;
    }
}
