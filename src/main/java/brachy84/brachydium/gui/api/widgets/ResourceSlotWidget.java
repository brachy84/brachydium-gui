package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.RenderObject;
import brachy84.brachydium.gui.internal.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * an abstract slot for handling resources
 * (like ItemStack or FluidStack)
 * @param <T> Resource f.e. ItemStack
 */
public abstract class ResourceSlotWidget<T> extends Widget implements Interactable {

    private final List<TextureArea> textures = new ArrayList<>();
    private final List<String> tags = new ArrayList<>();

    public ResourceSlotWidget(AABB bounds) {
        super(bounds);
    }

    public ResourceSlotWidget(Size size, Pos2d pos) {
        super(size, pos);
    }

    @Override
    public @Nullable RenderObject getRenderObject() {
        return ((matrices, delta) -> {
            if(textures.size() > 0) {
                textures.forEach(sprite -> guiHelper.drawTexture(matrices, sprite, getPos(), getSize()));
            } else {
                guiHelper.drawTexture(matrices, getFallbackTexture(), getPos());
            }
            renderResource(matrices);
        });
    }

    @Environment(EnvType.CLIENT)
    public abstract void renderResource(MatrixStack matrices);

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
    public abstract TextureArea getFallbackTexture();

    /**
     * @return the background sprites
     */
    public List<TextureArea> getTextures() {
        return textures;
    }

    /**
     * @param sprite to render
     * @return this, to use in a builder
     */
    public ResourceSlotWidget<T> addBackgroundSprites(TextureArea... sprite) {
        textures.addAll(Arrays.asList(sprite));
        return this;
    }

    /**
     * Tags provide an easy way to find specific slots in a gui
     * @param tag to check for
     * @return if the slot has the tag
     */
    public boolean hasTag(String tag) {
        for(String string : tags) {
            if(tag.equals(string)) {
                return true;
            }
        }
        return false;
    }

    public ResourceSlotWidget<T> addTag(String tag) {
        this.tags.add(tag.toLowerCase());
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
