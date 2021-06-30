package brachy84.brachydium.gui.api.math;

import java.util.Objects;

public record Size(float width, float height) {

    public static final Size ZERO = new Size(0, 0);

    public boolean isLargerThan(Size size) {
        return ((width - size.width) + (height - size.height)) > 0;
    }

    /**
     * @param size to center
     * @return the point of the top left corner
     */
    public Pos2d getCenteringPointForChild(Size size) {
        return new Pos2d((width - size.width) / 2, (height - size.height) / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return Float.compare(size.width, width) == 0 && Float.compare(size.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
