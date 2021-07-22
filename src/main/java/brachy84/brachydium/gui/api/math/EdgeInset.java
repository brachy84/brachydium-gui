package brachy84.brachydium.gui.api.math;

public record EdgeInset(float top, float bottom, float left, float right) {

    public EdgeInset {
        if(top < 0 || bottom < 0 || left < 0 || right < 0)
            throw new IllegalArgumentException("Margins can't be smaller than zero");
    }

    public static final EdgeInset ZERO = new EdgeInset(0, 0, 0, 0);

    public static EdgeInset all(float all) {
        return new EdgeInset(all, all, all, all);
    }

    public static EdgeInset horizontal(float left, float right) {
        return new EdgeInset(0, 0, left, right);
    }

    public static EdgeInset vertical(float top, float bottom) {
        return new EdgeInset(top, bottom, 0, 0);
    }

    public static EdgeInset top(float top) {
        return new EdgeInset(top, 0, 0, 0);
    }

    public static EdgeInset bottom(float bottom) {
        return new EdgeInset(0, bottom, 0, 0);
    }

    public static EdgeInset left(float left) {
        return new EdgeInset(0, 0, left, 0);
    }

    public static EdgeInset right(float right) {
        return new EdgeInset(0, 0, 0, right);
    }

    public EdgeInset withTop(float top) {
        return new EdgeInset(top, bottom, left, right);
    }

    public EdgeInset withBottom(float bottom) {
        return new EdgeInset(top, bottom, left, right);
    }

    public EdgeInset withLeft(float left) {
        return new EdgeInset(top, bottom, left, right);
    }

    public EdgeInset withRight(float right) {
        return new EdgeInset(top, bottom, left, right);
    }

    public boolean isZero() {
        return top == 0 && bottom == 0 && left == 0 && right == 0;
    }
}
