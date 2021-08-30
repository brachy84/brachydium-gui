package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.api.math.Alignment;
import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.Widget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;
import java.util.function.DoubleSupplier;

/**
 * A ProgressBarWidget is a widget that displays a bar which can show a progress or fill level
 * The move direction determines in which direction it fills up
 * seeAlso {@link ProgressTexture}, {@link MoveDirection}
 */
public class ProgressBarWidget extends Widget {

    private final ProgressTexture texture;
    private final DoubleSupplier progress;
    private MoveDirection moveDirection;

    /**
     * @param progress should supply a double between 0 and 1 where 0 is empty and 1 is full
     * @param texture texture to render
     */
    public ProgressBarWidget(DoubleSupplier progress, ProgressTexture texture) {
        this.progress = Objects.requireNonNull(progress);
        this.texture = Objects.requireNonNull(texture);
        this.moveDirection = MoveDirection.RIGHT;
    }

    public static ProgressBarWidget of(DoubleSupplier progress, ITexture full, ITexture empty) {
        return new ProgressBarWidget(progress, new ProgressTexture(empty, full));
    }

    public static ProgressBarWidget of(DoubleSupplier progress, TextureArea area) {
        return new ProgressBarWidget(progress, ProgressTexture.of(area));
    }

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        helper.drawTexture(matrices, texture.getEmpty(), getPos(), getSize());
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        float width = getSize().width(), height = getSize().height();
        switch (moveDirection) {
            case RIGHT -> {
                u1 = (float) progress.getAsDouble();
                width *= u1;
            }
            case LEFT -> {
                u0 = (float) (1 - progress.getAsDouble());
                width *= 1 - u0;
            }
            case UP -> {
                v0 = (float) (1 - progress.getAsDouble());
                height *= 1 - v0;
            }
            case DOWN -> {
                v1 = (float) progress.getAsDouble();
                height *= v1;
            }
        }
        TextureArea partFull = texture.getFull().getTexture().getSubArea(u0, v0, u1, v1);
        helper.drawTexture(matrices, partFull, getPos().add(u0, v0), new Size(width, height));
    }

    public ProgressBarWidget setMoveDirection(MoveDirection moveDirection) {
        this.moveDirection = Objects.requireNonNull(moveDirection);
        return this;
    }

    @Override
    public ProgressBarWidget setSize(Size size) {
        return (ProgressBarWidget) super.setSize(size);
    }

    @Override
    public ProgressBarWidget setAlignment(Alignment alignment) {
        return (ProgressBarWidget) super.setAlignment(alignment);
    }

    @Override
    public ProgressBarWidget setPos(Pos2d pos) {
        return (ProgressBarWidget) super.setPos(pos);
    }

    @Override
    public ProgressBarWidget setMargin(EdgeInset edgeInset) {
        return (ProgressBarWidget) super.setMargin(edgeInset);
    }

    public MoveDirection getMoveDirection() {
        return moveDirection;
    }
}
