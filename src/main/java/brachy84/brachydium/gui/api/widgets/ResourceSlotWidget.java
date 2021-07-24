package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.IDrawable;
import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * an abstract slot for handling resources
 * (like ItemStack or FluidStack)
 * @param <T> Resource f.e. ItemStack
 */
public abstract class ResourceSlotWidget<T> extends Widget implements Interactable {

    private final List<IDrawable> textures = new ArrayList<>();

    @Override
    public void render(IGuiHelper helper, MatrixStack matrices, float delta) {
        if(textures.size() > 0) {
            textures.forEach(sprite -> helper.drawTexture(matrices, sprite, getPos(), getSize()));
        } else {
            helper.drawTexture(matrices, getFallbackTexture(), getPos(), getSize());
        }

        if(!isEmpty())
            renderResource(helper, matrices);
        if(isInBounds(helper.getMousePos())) {
            renderHoveringOverlay(helper, matrices, delta);
        }
    }

    @Override
    public void renderForeground(IGuiHelper helper, MatrixStack matrices, float delta) {
        if(getGui().getCursor().isEmpty() && isInBounds(helper.getMousePos()) && !isEmpty())
            renderTooltip(helper, matrices, delta);
    }

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public abstract void renderResource(IGuiHelper helper, MatrixStack matrices);

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public void renderHoveringOverlay(IGuiHelper helper, MatrixStack matrices, float delta) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        helper.fillGradient(matrices, getPos().add(1, 1), new Size(16, 16), -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public abstract void renderTooltip(IGuiHelper helper, MatrixStack matrices, float delta);

    /**
     * Called when the player tries to insert something
     * @param resource to insert
     * @param player who inserts
     * @return if resource should be inserted
     */
    public boolean canPut(T resource, PlayerEntity player) {
        return true;
    }

    /**
     * Called when the player tries to take something
     * @param player who takes
     * @return if resource can be taken
     */
    public boolean canTake(PlayerEntity player) {
        return true;
    }

    public abstract T getResource();

    /**
     * @param resource to set
     * @param action see {@link Action}
     * @return if the resource was successfully set
     */
    public boolean setResource(T resource, Action action) {
        System.out.println("Try set resource " + resource + " with action " + action);
        if(action == Action.TAKE) {
            if(!canTake(getGui().player)) return false;
        } else if(action == Action.PUT) {
            if(!canPut(resource, getGui().player)) return false;
        }
        return setResource(resource);
    }

    /**
     * This method should not be called. Use {@link ResourceSlotWidget#setResource(Object, Action)} instead
     * @param resource to set
     * @return if the resource was successfully set
     */
    @ApiStatus.OverrideOnly
    public abstract boolean setResource(T resource);

    public abstract boolean isEmpty();

    /**
     * @return the fallback background to render if it has no sprites
     */
    public abstract IDrawable getFallbackTexture();

    /**
     * @return the background sprites
     */
    public List<IDrawable> getTextures() {
        return textures;
    }

    /**
     * @param sprite to render
     * @return this, to use in a builder
     */
    public ResourceSlotWidget<T> addBackgroundSprites(IDrawable... sprite) {
        textures.addAll(Arrays.asList(sprite));
        return this;
    }

    public enum Action {

        /**
         * When the resource gets taken from the slot
         * Triggers {@link #canTake(PlayerEntity)}
         */
        TAKE,

        /**
         * When the resource is put into the slot
         * Triggers {@link #canPut(Object, PlayerEntity)}
         */
        PUT,

        /**
         * When the slot is synced to client
         * Triggers nothing
         */
        SYNC
    }
}
