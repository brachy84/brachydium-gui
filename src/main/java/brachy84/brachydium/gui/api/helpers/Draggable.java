package brachy84.brachydium.gui.api.helpers;

import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.Widget;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public interface Draggable extends Interactable {

    /**
     * Get's called from the cursor
     * Usually you just call {@link Widget#render(MatrixStack, Pos2d, float)}
     * No need to translate
     *
     * @param matrices matrix stack
     * @param mousePos current mouse pos
     * @param delta    difference from last from
     */
    void renderMovingState(MatrixStack matrices, Pos2d mousePos, float delta);

    /**
     * @param button the mouse button that's holding down
     * @return false if the action should be canceled
     */
    boolean onDragStart(int button);

    /**
     * The dragging has ended and getState == IDLE
     *
     * @param successful is false if this returned to it's old position
     */
    void onDragEnd(boolean successful);

    /**
     * Gets called when the mouse is released
     *
     * @param widget     current top most widget below the mouse
     * @param mousePos   current mousePos
     * @param isInBounds if the mouse is in the gui bounds
     * @return if the location is valid
     */
    default boolean canDropHere(@Nullable Widget widget, Pos2d mousePos, boolean isInBounds) {
        return isInBounds;
    }

    default boolean shouldRenderChildren() {
        return true;
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
