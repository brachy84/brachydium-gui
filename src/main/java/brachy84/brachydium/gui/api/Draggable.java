package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.Widget;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public interface Draggable extends Interactable {

    void renderMovingState(IGuiHelper helper, MatrixStack matrices, float delta);

    /**
     * @param button the mouse button that's holding down
     * @return false if the action should be canceled
     */
    boolean onDragStart(int button);

    /**
     * The dragging has ended and getState == IDLE
     * @param successful is false if this returned to it's old position
     */
    void onDragEnd(boolean successful);

    /**
     * Gets called when the mouse is released
     * @param widget current top most widget below the mouse
     * @param mousePos current mousePos
     * @param isInBounds if the mouse is in the gui bounds
     * @return if the location is valid
     */
    default boolean canDropHere(@Nullable Widget widget, Pos2d mousePos, boolean isInBounds) {
        return isInBounds;
    }

    State getState();

    void setState(State state);

    default boolean isIdle() {
        return getState() == State.IDLE;
    }

    default boolean isMoving() {
        return getState() == State.MOVING;
    }

    enum State {
        IDLE,
        MOVING
    }
}
