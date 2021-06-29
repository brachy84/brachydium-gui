package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.IGuiHelper;
import brachy84.brachydium.gui.api.TextureArea;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public interface GuiHelper extends IGuiHelper {

    @Override
    default TextRenderer getTextRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }

    @Override
    default void drawText(MatrixStack matrices, Text text, Pos2d pos) {
        getTextRenderer().draw(matrices, text, pos.x, pos.y, (int) getZ());
    }

    @Override
    default void drawText(MatrixStack matrices, String text, Pos2d pos) {
        drawText(matrices, new LiteralText(text), pos);
    }

    @Override
    default void drawItem(MatrixStack matrices, ItemStack item, Pos2d pos) {
        ItemRenderer ir = MinecraftClient.getInstance().getItemRenderer();
        ir.zOffset = getZ();
        ir.renderInGuiWithOverrides(item, (int) pos.x, (int) pos.y);
        ir.renderGuiItemOverlay(getTextRenderer(), item, (int) pos.x, (int) pos.y);
    }

    @Override
    default void drawFluid(MatrixStack matrices, Fluid fluid, String amount, Pos2d pos, Size size) {
        if (fluid == Fluids.EMPTY) return;
        setZ(getZ() + 50);
        SimpleFluidRenderer.renderInGui(matrices, fluid, AABB.of(size, pos), getZ());
        matrices.translate(0, 0, getZ() + 5);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        getTextRenderer().draw(amount, pos.x + 19 - 2 - getTextRenderer().getWidth(amount), pos.y + 6 + 3, 16777215, true, matrices.peek().getModel(), immediate, false, 0, 15728880);
        immediate.draw();
        setZ(getZ() - 50);
    }

    @Override
    default void drawTexture(MatrixStack matrices, TextureArea texture, Pos2d pos) {
        drawTexture(matrices, texture, pos, texture.getImageSize());
    }

    @Override
    default void drawTexture(MatrixStack matrices, TextureArea texture, Pos2d pos, Size drawSize) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture.getPath());
        //matrices.color4f(1f, 1f, 1f, 1f);
        Matrix4f matrix4f = matrices.peek().getModel();

        AABB bounds = AABB.of(drawSize, pos);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y1, getZ()).texture(texture.u0, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y1, getZ()).texture(texture.u1, texture.v1).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y0, getZ()).texture(texture.u1, texture.v0).next();
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y0, getZ()).texture(texture.u0, texture.v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    @Override
    default void drawTooltip(MatrixStack matrices, List<? extends OrderedText> lines, Pos2d pos) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen != null) {
            screen.renderOrderedTooltip(matrices, lines, (int) pos.x, (int) pos.y);
        }
        //TODO: fallback drawer
    }
}
