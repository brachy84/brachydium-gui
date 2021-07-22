package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.Color;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface IGuiHelper {

    /**
     * @return z layer
     */
    float getZ();

    /**
     * @return current mouse pos
     */
    Pos2d getMousePos();

    /**
     * @param z new z layer
     */
    void setZ(float z);

    /**
     * @param amount to increase z layer by
     */
    default void incrementZ(float amount) {
        setZ(getZ() + amount);
    }

    /**
     * @param amount to decrease z layer by
     */
    default void decrementZ(float amount) {
        setZ(getZ() - amount);
    }

    /**
     * @return text renderer
     */
    TextRenderer getTextRenderer();

    /**
     * Draws a text in gui
     *
     * @param matrices matrices
     * @param text     text
     * @param pos      pos
     */
    void drawText(MatrixStack matrices, Text text, Pos2d pos);

    /**
     * Draws a string in gui
     *
     * @param matrices matrices
     * @param text     text
     * @param pos      pos
     */
    void drawText(MatrixStack matrices, String text, Pos2d pos);

    /**
     * Draws a string in gui with amount text and durability bar
     *
     * @param matrices matrices
     * @param item     itemStack
     * @param pos      pos
     */
    void drawItem(MatrixStack matrices, ItemStack item, Pos2d pos);

    /**
     * Draws a fluid rectangle in gui
     *
     * @param matrices matrices
     * @param fluid    fluid to render
     * @param amount   amount to draw on top
     * @param pos      top left corner
     * @param size     size
     */
    void drawFluid(MatrixStack matrices, Fluid fluid, String amount, Pos2d pos, Size size);

    /**
     * Draws a drawable (f.e. a {@link TextureArea})
     *
     * @param matrices matrices
     * @param texture  drawable
     * @param pos      top left corner
     * @param size     draw size
     */
    void drawTexture(MatrixStack matrices, IDrawable texture, Pos2d pos, Size size);

    /**
     * Draws a vanilla style tooltip
     *
     * @param matrices matrices
     * @param lines    text
     * @param pos      this can be mouse pos (no need to translate)
     */
    void drawTooltip(MatrixStack matrices, List<? extends OrderedText> lines, Pos2d pos);

    /**
     * Fills an rectangle with a color
     *
     * @param matrices matrices
     * @param pos      top left corner
     * @param size     size
     * @param color    color {@link Color#asInt()}
     */
    void fill(MatrixStack matrices, Pos2d pos, Size size, int color);

    /**
     * Fills an rectangle with a gradient
     *
     * @param matrices   matrices
     * @param pos        top left corner
     * @param size       size
     * @param colorStart start color, see {@link Color#asInt()}
     * @param colorEnd   end color, see {@link Color#asInt()}
     */
    void fillGradient(MatrixStack matrices, Pos2d pos, Size size, int colorStart, int colorEnd);

}
