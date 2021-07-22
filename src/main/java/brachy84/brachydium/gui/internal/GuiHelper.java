package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.IDrawable;
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

    static GuiHelper create(float layer, Pos2d pos) {
        return new GuiHelper() {
            private final Pos2d mousePos = pos;
            private float z = layer;

            @Override
            public float getZ() {
                return z;
            }

            @Override
            public Pos2d getMousePos() {
                return mousePos;
            }

            @Override
            public void setZ(float z) {
                this.z = z;
            }
        };
    }

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
        //setZ(getZ() + 50);
        SimpleFluidRenderer.renderInGui(matrices, fluid, AABB.of(size, pos), getZ());
        //matrices.translate(0, 0, getZ() + 5);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        getTextRenderer().draw(amount, pos.x + 19 - 2 - getTextRenderer().getWidth(amount), pos.y + 6 + 3, 16777215, true, matrices.peek().getModel(), immediate, false, 0, 15728880);
        immediate.draw();
        //setZ(getZ() - 50);
    }

    @Override
    default void drawTexture(MatrixStack matrices, IDrawable drawable, Pos2d pos, Size drawSize) {
        Matrix4f matrix4f = matrices.peek().getModel();

        AABB bounds = AABB.of(drawSize, pos);
        TextureArea texture = drawable.getTexture();

        RenderSystem.setShaderTexture(0, texture.getPath());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y1, getZ()).texture(texture.u0(), texture.v1()).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y1, getZ()).texture(texture.u1(), texture.v1()).next();
        bufferBuilder.vertex(matrix4f, bounds.x1, bounds.y0, getZ()).texture(texture.u1(), texture.v0()).next();
        bufferBuilder.vertex(matrix4f, bounds.x0, bounds.y0, getZ()).texture(texture.u0(), texture.v0()).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    @Override
    default void drawTooltip(MatrixStack matrices, List<? extends OrderedText> lines, Pos2d pos) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null) {
            screen.renderOrderedTooltip(matrices, lines, (int) pos.x, (int) pos.y);
        }
        //TODO: fallback drawer
    }

    default void fill(MatrixStack matrices, Pos2d pos, Size size, int color) {
        Matrix4f matrix = matrices.peek().getModel();
        float j, x1 = pos.x, x2 = x1 + size.width(), y1 = pos.y, y2 = y1 + size.height();
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, getZ()).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x2, y2, getZ()).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x2, y1, getZ()).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x1, y1, getZ()).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    default void fillGradient(MatrixStack matrices, Pos2d pos, Size size, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder, pos, size, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    default void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, Pos2d pos, Size size, int colorStart, int colorEnd) {
        float x0 = pos.x, y0 = pos.y, x1 = x0 + size.width(), y1 = y0 + size.height();
        float f = (float) (colorStart >> 24 & 255) / 255.0F;
        float g = (float) (colorStart >> 16 & 255) / 255.0F;
        float h = (float) (colorStart >> 8 & 255) / 255.0F;
        float i = (float) (colorStart & 255) / 255.0F;
        float j = (float) (colorEnd >> 24 & 255) / 255.0F;
        float k = (float) (colorEnd >> 16 & 255) / 255.0F;
        float l = (float) (colorEnd >> 8 & 255) / 255.0F;
        float m = (float) (colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrix, x1, y0, getZ()).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, x0, y0, getZ()).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, x0, y1, getZ()).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, x1, y1, getZ()).color(k, l, m, j).next();
    }
}