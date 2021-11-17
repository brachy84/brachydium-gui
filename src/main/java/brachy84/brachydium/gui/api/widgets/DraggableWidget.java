package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.helpers.Draggable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class DraggableWidget extends SingleChildWidget implements Draggable {

    private State state;
    private Function<Integer, Boolean> onDragStart;
    private Consumer<Boolean> onDragEnd;

    public DraggableWidget(Widget child) {
        this.state = State.IDLE;
        this.onDragStart = (v) -> true;
        this.onDragEnd = (v) -> {};
        setChildInternal(child);
    }

    @Override
    public void rePosition() {
        super.rePosition();
        setSize(getChild().getSize());
    }

    @Override
    public void renderMovingState(MatrixStack matrices, Pos2d mousePos, float delta) {
    }

    @Override
    public boolean onDragStart(int button) {
        return onDragStart.apply(button);
    }

    @Override
    public void onDragEnd(boolean successful) {
        onDragEnd.accept(successful);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean mustHaveChild() {
        return true;
    }

    public DraggableWidget setOnDragStart(Function<Integer, Boolean> onDragStart) {
        this.onDragStart = Objects.requireNonNull(onDragStart);
        return this;
    }

    public DraggableWidget setOnDragEnd(Consumer<Boolean> onDragEnd) {
        this.onDragEnd = Objects.requireNonNull(onDragEnd);
        return this;
    }

    @Override
    public void readServerData(int id, PacketByteBuf buf) {
    }

    @Override
    public void readClientData(int id, PacketByteBuf buf) {
    }
}
