package brachy84.brachydium.gui.api;

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

    float getZ();

    Pos2d getMousePos();

    void setZ(float z);

    default void incrementZ(float amount) {
        setZ(getZ() + amount);
    }

    default void decrementZ(float amount) {
        setZ(getZ() - amount);
    }

    TextRenderer getTextRenderer();

    void drawText(MatrixStack matrices, Text text, Pos2d pos);

    void drawText(MatrixStack matrices, String text, Pos2d pos);

    void drawItem(MatrixStack matrices, ItemStack item, Pos2d pos);

    void drawFluid(MatrixStack matrices, Fluid fluid, String amount, Pos2d pos, Size size);

    void drawTexture(MatrixStack matrices, IDrawable texture, Pos2d pos);

    void drawTexture(MatrixStack matrices, IDrawable texture, Pos2d pos, Size size);

    void drawTooltip(MatrixStack matrices, List<? extends OrderedText> lines, Pos2d pos);

    void fill(MatrixStack matrices, Pos2d pos, Size size, int color);

    void fillGradient(MatrixStack matrices, Pos2d pos, Size size, int colorStart, int colorEnd);

    /*void drawHorizontalLine(Point start, float length, Color color);

    void drawVerticalLine(Point start, float length, Color color);

    void drawRect(Point pos, Size size, Color color);*/
}
