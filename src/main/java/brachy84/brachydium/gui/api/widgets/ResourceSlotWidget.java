package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.ITexture;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.GuiHelper;
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

    private final List<ITexture> textures = new ArrayList<>();

    @Override
    public void render(MatrixStack matrices, Pos2d mousePos, float delta) {
        // item renderer only accepts int as pos and you can not translate the item via matrices so i have to move the slot
        // wtf mojank
        matrices.push();
        matrices.translate(0, -0.5, 0);
        if(textures.size() > 0) {
            textures.forEach(sprite -> GuiHelper.drawTexture(matrices, sprite, getPos(), getSize()));
        } else {
            GuiHelper.drawTexture(matrices, getFallbackTexture(), getPos(), getSize());
        }

        if(!isEmpty())
            renderResource(matrices, mousePos);
        if(isInBounds(mousePos)) {
            renderHoveringOverlay(matrices, delta);
        }
        matrices.pop();
    }

    @Override
    public void renderForeground(MatrixStack matrices, Pos2d mousePos, float delta) {
        if(getGui().getCursorStack().isEmpty() && isInBounds(mousePos) && !isEmpty())
            renderTooltip(matrices, mousePos, delta);
    }

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public abstract void renderResource(MatrixStack matrices, Pos2d mousePos);

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public void renderHoveringOverlay(MatrixStack matrices, float delta) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        GuiHelper.fillGradient(matrices, getPos().add(1, 1), new Size(16, 16), -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Environment(EnvType.CLIENT)
    @ApiStatus.OverrideOnly
    public abstract void renderTooltip(MatrixStack matrices, Pos2d mousePos, float delta);

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
    public abstract ITexture getFallbackTexture();

    /**
     * @return the background sprites
     */
    public List<ITexture> getTextures() {
        return textures;
    }

    /**
     * @param sprite to render
     * @return this, to use in a builder
     */
    public ResourceSlotWidget<T> addBackgroundSprites(ITexture... sprite) {
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
