package brachy84.brachydium.gui.api;

public class ProgressTexture {

    private final ITexture empty, full;

    public ProgressTexture(ITexture empty, ITexture full) {
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

    public ITexture getEmpty() {
        return empty;
    }

    public ITexture getFull() {
        return full;
    }
}
