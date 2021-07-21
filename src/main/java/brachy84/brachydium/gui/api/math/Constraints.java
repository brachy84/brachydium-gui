package brachy84.brachydium.gui.api.math;

public record Constraints(Size minSize, Size maxSize) {

    public static Constraints of(float minWidth, float minHeight, float maxWidth, float maxHeight) {
        return new Constraints(new Size(minWidth, minHeight), new Size(maxWidth, maxHeight));
    }

    public static Constraints ofMin(float minWidth, float minHeight) {
        return Constraints.of(minWidth, minHeight, Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public static Constraints ofMax(float maxWidth, float maxHeight) {
        return Constraints.of(0, 0, maxWidth, maxHeight);
    }

    public static Constraints copyWithMin(Constraints constraints, float minWidth, float minHeight) {
        return Constraints.of(minWidth, minHeight, constraints.maxWidth(), constraints.maxHeight());
    }

    public static Constraints copyWithMax(Constraints constraints, float maxWidth, float maxHeight) {
        return Constraints.of(constraints.minWidth(), constraints.minHeight(), maxWidth, maxHeight);
    }

    public float minWidth() {
        return minSize.width();
    }

    public float minHeight() {
        return minSize.height();
    }

    public float maxWidth() {
        return maxSize.width();
    }

    public float maxHeight() {
        return maxSize.height();
    }

    public boolean matches(Size size) {
        return minSize.width() <= size.width() &&
                minSize.height() <= size.height() &&
                maxSize.width() >= size.width() &&
                maxSize.height() >= size.height();
    }

    public boolean doesCover(Constraints constraints) {
        return minSize.width() >= constraints.minSize.width() &&
                minSize.height() >= constraints.minSize.height() &&
                maxSize.width() >= constraints.maxSize.width() &&
                maxSize.height() >= constraints.maxSize.height();
    }

    public Size clamp(Size size) {
        float w = Math.min(maxSize.width(), Math.max(minSize.width(), size.width()));
        float h = Math.min(maxSize.height(), Math.max(minSize.height(), size.height()));
        return new Size(w, h);
    }
}
