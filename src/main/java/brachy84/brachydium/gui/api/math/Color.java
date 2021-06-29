package brachy84.brachydium.gui.api.math;

import java.util.Objects;

public class Color {

    private byte r, g, b, a;

    public Color(byte red, byte green, byte blue, byte alpha) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    public static Color of(int red, int green, int blue, int alpha) {
        return new Color((byte) red, (byte) green, (byte) blue, (byte) alpha);
    }

    public static Color of(int red, int green, int blue) {
        return of(red, green, blue, 255);
    }

    public static Color of(float red, float green, float blue, float alpha) {
        return new Color((byte) (red * 255), (byte) (green * 255), (byte) (blue * 255), (byte) (alpha * 255));
    }

    public static Color of(float red, float green, float blue) {
        return of(red, green, blue, 1f);
    }

    public static Color of(int color) {
        byte a = (byte) (color >> 24 & 255);
        byte r = (byte) (color >> 16 & 255);
        byte g = (byte) (color >> 8 & 255);
        byte b = (byte) (color & 255);
        return new Color(r, g, b, a);
    }

    public Color withRed(byte r) {
        return new Color(r, g, b, a);
    }

    public Color withBlue(byte b) {
        return new Color(r, g, b, a);
    }

    public Color withGreen(byte g) {
        return new Color(r, g, b, a);
    }

    public Color withAlpha(byte a) {
        return new Color(r, g, b, a);
    }

    public Color withOpacity(double opacity) {
        byte a = (byte) (255 * opacity);
        return new Color(r, g, b, a);
    }

    public byte getRed() {
        return r;
    }

    public byte getGreen() {
        return g;
    }

    public byte getBlue() {
        return b;
    }

    public byte getAlpha() {
        return a;
    }

    public int asInt() {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && a == color.a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }
}
