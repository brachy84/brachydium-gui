package brachy84.brachydium.gui.api.rendering;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import net.minecraft.util.Identifier;

/**
 * Defines an area in a png image
 */
public record TextureArea(Identifier path, float u0,
                          float v0, float u1, float v1) implements ITexture {

    public TextureArea {
        if (!path.getPath().endsWith(".png")) {
            path = new Identifier(path.getNamespace(), path.getPath() + ".png");
        }
        if (!path.getPath().startsWith("textures/")) {
            path = new Identifier(path.getNamespace(), "textures/" + path.getPath());
        }
    }

    public static TextureArea fullImage(Identifier path) {
        return new TextureArea(path, 0, 0, 1, 1);
    }

    public static TextureArea fullImage(String mod, String path) {
        return fullImage(new Identifier(mod, path));
    }

    public static TextureArea of(Identifier path, float u, float v) {
        return new TextureArea(path, u, v, 1, 1);
    }

    public static TextureArea of(Identifier path, float u0, float v0, float u1, float v1) {
        return new TextureArea(path, u0, v0, u1, v1);
    }

    public TextureArea getSubArea(float u, float v) {
        return TextureArea.of(path, u0 + u0 * u, v0 + v0 * v);
    }

    public TextureArea getSubArea(AABB bounds) {
        return getSubArea(bounds.x0, bounds.y0, bounds.x1, bounds.y1);
    }

    public TextureArea getSubArea(Pos2d pos, Size size) {
        return getSubArea(AABB.of(size, pos));
    }

    public TextureArea getSubArea(float u0, float v0, float u1, float v1) {
        return TextureArea.of(path, calcUV0(this.u0, u0), calcUV0(this.v0, v0), this.u1 * u1, this.v1 * v1);
    }

    public Identifier getPath() {
        return path;
    }

    private float calcUV0(float oldV, float newV) {
        return oldV == 0.0F ? oldV + newV : oldV + oldV * newV;
    }

    @Override
    public TextureArea getTexture() {
        return this;
    }
}
