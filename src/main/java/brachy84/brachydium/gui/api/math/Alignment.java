package brachy84.brachydium.gui.api.math;

public record Alignment(float x, float y) {

    public static final Alignment TopLeft       = new Alignment(-1, -1);
    public static final Alignment TopCenter     = new Alignment(0, -1);
    public static final Alignment TopRight      = new Alignment(1, -1);
    public static final Alignment CenterLeft    = new Alignment(-1, 0);
    public static final Alignment Center        = new Alignment(0, 0);
    public static final Alignment CenterRight   = new Alignment(1, 0);
    public static final Alignment BottomLeft    = new Alignment(-1, 1);
    public static final Alignment BottomCenter  = new Alignment(0, 1);
    public static final Alignment BottomRight   = new Alignment(1, 1);

    public static final Alignment[] ALL = {
            TopLeft,    TopCenter,      TopRight,
            CenterLeft, Center,         CenterRight,
            BottomLeft, BottomCenter,   BottomRight
    };

    public static final Alignment[] CORNERS = {
            TopLeft,    TopRight,
            BottomLeft, BottomRight
    };

    public Pos2d getAlignedPos(Size parent, Size child) {
        float x = (this.x + 1) / 2, y = (this.y + 1) / 2;
        return new Pos2d(parent.width() * x - child.width() * x, parent.height() * y - child.height() * y);
    }
}
