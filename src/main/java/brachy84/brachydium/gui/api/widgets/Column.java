package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.internal.Widget;

public class Column extends Widget implements MultiChildWidget {

    public Column() {
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public Column child(Widget child) {
        addChild(child);
        return this;
    }

    @Override
    public Column children(Widget... children) {
        for(Widget widget : children) {
            addChild(widget);
        }
        return this;
    }
}
