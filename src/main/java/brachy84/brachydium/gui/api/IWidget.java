package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;

public interface IWidget {

    /**
     * @return the absolute Pos of the Parent
     */
    Pos2d getParentPos();

    /**
     * @return the Pos relative to it's parent
     */
    Pos2d getPos();

    /**
     * @return the Pos to 0, 0
     */
    Pos2d getAbsolutePos();

    /**
     * @return the width and height as Size
     */
    Size getSize();

    default AABB getBounds() {
        return AABB.of(getSize(), getAbsolutePos());
    }

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
