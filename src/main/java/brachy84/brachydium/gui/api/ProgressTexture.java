package brachy84.brachydium.gui.api;

public class ProgressTexture {

    private TextureArea empty, full;

    public ProgressTexture(TextureArea empty, TextureArea full) {
        this.empty = empty;
        this.full = full;
    }

    /**
     * @param area a texture where the full and empty are on top of each other
     * @return progress texture
     */
    public static ProgressTexture of(TextureArea area) {
        return new ProgressTexture(area.getSubArea(0, 0, 1, 0.5f), area.getSubArea(0, 0.5f, 1, 1));
    }

    public TextureArea getEmpty() {
        return empty;
    }

    public TextureArea getFull() {
        return full;
    }
}
