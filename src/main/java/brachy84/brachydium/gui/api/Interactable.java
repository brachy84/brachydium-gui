package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.Pos2d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * An interface that handles user interactions.
 * These methods get called on the client
 */
public interface Interactable extends ISyncedWidget {

    /**
     * called when ever the mouse moves on the screen
     * @param pos of the mouse
     */
    @ApiStatus.OverrideOnly
    default void onMouseMoved(Pos2d pos) {}

    /**
     * called when clicked on the Interactable based on {@link Interactable#isMouseOver(Pos2d)}
     * @param pos of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     */
    @ApiStatus.OverrideOnly
    default void onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {}

    /**
     * called when released a click on the Interactable based on {@link Interactable#isMouseOver(Pos2d)}
     * @param pos of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     */
    @ApiStatus.OverrideOnly
    default void onClickReleased(Pos2d pos, int buttonId) {}

    /**
     * called when the interactable is focused and the mouse gets dragged
     * @param pos of the mouse
     * @param buttonId the button id (Left == 1, right == 2)
     * @param deltaX difference from last call
     * @param deltaY difference from last call
     */
    @ApiStatus.OverrideOnly
    default void onMouseDragged(Pos2d pos, int buttonId, double deltaX, double deltaY) {}

    /**
     * called when the interactable is focused and the scrollweel is used
     * @param pos of the mouse
     * @param amount of lines scrolled
     */
    @ApiStatus.OverrideOnly
    default void onScrolled(Pos2d pos, double amount) {}

    /**
     * called when the interactable is focused and a key is pressed
     * @param keyCode key
     * @param scanCode ?
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onKeyPressed(int keyCode, int scanCode, int modifiers) {}

    /**
     * called when the interactable is focused and a key is released
     * @param keyCode key
     * @param scanCode ?
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onKeyReleased(int keyCode, int scanCode, int modifiers) {}

    /**
     * called when the interactable is focused and a char is typed
     * @param chr character
     * @param modifiers ?
     */
    @ApiStatus.OverrideOnly
    default void onCharTyped(char chr, int modifiers) {}

    /**
     * try change the focus
     * @param lookForwards should look for next focus
     * Not yet implemented
     */
    @Deprecated
    @ApiStatus.NonExtendable
    default void changeFocus(boolean lookForwards) {}

    /**
     * @param pos to check
     * @return if the point is within the parents bounds
     */
    boolean isMouseOver(Pos2d pos);

    /**
     * @return if left or right ctrl/cmd is pressed
     */
    @Environment(EnvType.CLIENT)
    static boolean hasControlDown() {
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return isKeyPressed(GLFW.GLFW_KEY_LEFT_SUPER) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SUPER);
        } else {
            return isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
    }

    /**
     * @return if left or right shift is pressed
     */
    @Environment(EnvType.CLIENT)
    static boolean hasShiftDown() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    /**
     * @return if alt or alt gr is pressed
     */
    @Environment(EnvType.CLIENT)
    static boolean hasAltDown() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    /**
     * @param codec of the key, see {@link GLFW}
     * @return if the key of codec is pressed
     */
    @Environment(EnvType.CLIENT)
    static boolean isKeyPressed(int codec) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), codec);
    }
}
