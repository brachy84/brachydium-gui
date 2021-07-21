package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.math.Constraints;
import brachy84.brachydium.gui.api.math.Size;

public class SizeInfo {

    public final Size preferredSize;
    public final Constraints tolerance;

    public SizeInfo(Size preferredSize, Constraints tolerance) {
        if(!tolerance.matches(preferredSize)) {
            throw new IllegalArgumentException("Preferred size is not in tolerance");
        }
        this.preferredSize = preferredSize;
        this.tolerance = tolerance;
    }
}
