package brachy84.brachydium.gui.api.math;

public class Gradient extends Color {

    public Gradient(byte red, byte green, byte blue, byte alpha) {
        super(red, green, blue, alpha);
    }

    public enum Type {
        LINEAR,
        CIRCULAR
    }
}
