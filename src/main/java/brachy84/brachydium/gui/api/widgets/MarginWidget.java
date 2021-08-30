package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.math.EdgeInset;
import brachy84.brachydium.gui.internal.Widget;

public class MarginWidget extends SingleChildWidget {

    public MarginWidget(EdgeInset margin, Widget child) {
        setChildInternal(child);
        setMargin(margin);
    }
}
