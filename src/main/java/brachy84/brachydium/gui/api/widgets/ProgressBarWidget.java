package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.helpers.*;
import brachy84.brachydium.gui.api.math.*;
import brachy84.brachydium.gui.api.rendering.*;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;

/**
 * A ProgressBarWidget is a widget that displays a bar which can show a progress or fill level
 * The move direction determines in which direction it fills up
 * seeAlso {@link ProgressTexture}, {@link MoveDirection}
 */
public class ProgressBarWidget extends Widget implements ISyncedWidget {

    private final ProgressTexture texture;
    private final DoubleSupplier progress;
    private MoveDirection moveDirection;
    private double currentProgress;

    /**
     * @param progress should supply a double between 0 and 1 where 0 is empty and 1 is full
     * @param texture  texture to render
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
    public void tick() {
        if (!Gui.isClient() && currentProgress != progress.getAsDouble()) {
            currentProgress = progress.getAsDouble();
            sendToClient(getGui().player);
        }
    }

    @Override
    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        drawBar(matrices, getPos(), currentProgress);
    }

    private void drawBar(MatrixStack matrices, Pos2d pos, double progress) {
        GuiHelper.drawTexture(matrices, texture.getEmpty(), pos, getSize());
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        float width = getSize().width(), height = getSize().height();
        switch (moveDirection) {
            case RIGHT -> {
                u1 = (float) progress;
                width *= u1;
            }
            case LEFT -> {
                u0 = (float) (1 - progress);
                width *= 1 - u0;
            }
            case UP -> {
                v0 = (float) (1 - progress);
                height *= 1 - v0;
            }
            case DOWN -> {
                v1 = (float) progress;
                height *= v1;
            }
        }
        TextureArea partFull = texture.getFull().getTexture().getSubArea(u0, v0, u1, v1);
        GuiHelper.drawTexture(matrices, partFull, pos.add(u0, v0), new Size(width, height));
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

    @Override
    public List<me.shedaniel.rei.api.client.gui.widgets.Widget> getReiWidgets(AABB bounds, Pos2d reiPos) {
        List<me.shedaniel.rei.api.client.gui.widgets.Widget> widgets = new ArrayList<>();
        me.shedaniel.rei.api.client.gui.widgets.Widget render = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            drawBar(matrices, reiPos, progress.getAsDouble());
        }));
        widgets.add(render);
        return widgets;
    }

    @Override
    public void readData(boolean fromServer, PacketByteBuf data) {
        if (fromServer) {
            currentProgress = data.readDouble();
        }
    }

    @Override
    public void writeData(boolean fromServer, PacketByteBuf data) {
        if (fromServer) {
            data.writeDouble(currentProgress);
        }
    }
}
