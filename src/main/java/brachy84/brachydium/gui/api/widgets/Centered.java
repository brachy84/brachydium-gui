package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.math.Alignment;

import java.util.Objects;

/**
 * Center itself into the parent
 */
public class Centered extends SingleChildWidget {

    public Centered(Widget child) {
        Objects.requireNonNull(child);
        setSize(child.getSize());
        setAlignment(Alignment.Center);
        addChild(child);
    }
}
