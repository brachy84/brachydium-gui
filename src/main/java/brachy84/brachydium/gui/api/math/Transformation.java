package brachy84.brachydium.gui.api.math;

public class Transformation {

    public static final Transformation ZERO = new Transformation(0, 0, 0, 0, 0, 0);

    private float scaleY, scaleX, rotation, transX, transY, transZ;
    private Pos2d center;
    private int z;

    public Transformation(float scaleX, float scaleY, float rotation, float transX, float transY, float transZ) {
        this.scaleY = scaleY;
        this.scaleX = scaleX;
        this.rotation = rotation;
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
        this.center = Pos2d.ZERO;
    }

    public static Transformation scale(float x, float y) {
        return new Transformation(x, y, 0, 0, 0, 0);
    }

    public static Transformation rotate(float angle) {
        return new Transformation(0, 0, angle, 0, 0, 0);
    }

    public static Transformation translate(float x, float y) {
        return new Transformation(0, 0, 0, x, y, 0);
    }

    public static Transformation translate(float x, float y, float z) {
        return new Transformation(0, 0, 0, x, y, z);
    }

    public Transformation scaleX(float x) {
        this.scaleX += x;
        return this;
    }

    public Transformation scaleY(float y) {
        this.scaleY += y;
        return this;
    }

    public Transformation resetScale() {
        this.scaleX = 0;
        this.scaleY = 0;
        return this;
    }

    public Transformation rotateZ(float angle) {
        this.rotation += angle;
        return this;
    }

    public Transformation resetRotation() {
        this.rotation = 0;
        return this;
    }

    public Transformation translateX(float x) {
        this.transX += x;
        return this;
    }

    public Transformation translateY(float y) {
        this.transY += y;
        return this;
    }

    public Transformation translateZ(float z) {
        this.transZ += z;
        return this;
    }

    public Transformation resetTranslation() {
        this.transX = 0;
        this.transY = 0;
        this.transZ = 0;
        return this;
    }

    /*public void applyRotation() {
        RenderSystem.rotatef(rotation, center.getX(), center.getY(), z);
    }

    public void applyScale() {
        RenderSystem.scalef(scaleX, scaleY, 0);
    }

    public void applyTranslation() {
        RenderSystem.translatef(transX, transY, transZ);
    }*/

    public void setRotationVector(Pos2d pos, int z) {
        this.center = pos;
        this.z = z;
    }
}
