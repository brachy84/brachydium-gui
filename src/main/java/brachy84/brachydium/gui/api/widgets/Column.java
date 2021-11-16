package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.Layout.CrossAxisAlignment;
import brachy84.brachydium.gui.api.widgets.Layout.MainAxisAlignment;
import brachy84.brachydium.gui.api.Widget;

public class Column extends MultiChildWidget {

    private CrossAxisAlignment crossAxisAlignment;
    private MainAxisAlignment mainAxisAlignment;

    public Column() {
        crossAxisAlignment = CrossAxisAlignment.START;
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void layoutChildren() {
        MainAxisAlignment maa = mainAxisAlignment;
        float totalHeight = 0;
        for (Widget widget : getChildren()) {
            totalHeight += widget.getSize().height();
        }
        if (totalHeight > getSize().height())
            maa = MainAxisAlignment.START;
        float start = 0;
        float spacing = 0;
        switch (maa) {
            case SPACE_BETWEEN -> spacing = (getSize().height() - totalHeight) / (getChildren().size() - 1);
            case END -> start = getSize().height() - totalHeight;
            case CENTER -> start = getSize().height() / 2 - totalHeight / 2;
            case SPACE_EVEN -> {
                start = (getSize().height() - totalHeight) / (getChildren().size() + 1);
                spacing = start;
            }
            case SPACE_AROUND -> {
                start = (getSize().height() - totalHeight) / (getChildren().size());
                spacing = start;
            }
        }
        float y = start;
        for (Widget widget : getChildren()) {
            widget.setPos(getX(widget), y);
            y += widget.getSize().height() + spacing;
        }
    }

    private float getX(Widget widget) {
        float x = (crossAxisAlignment.value + 1) / 2;
        return getSize().width() * x;
    }

    public Column setCrossAxisAlignment(CrossAxisAlignment crossAxisAlignment) {
        this.crossAxisAlignment = crossAxisAlignment;
        return this;
    }

    public Column setMainAxisAlignment(MainAxisAlignment mainAxisAlignment) {
        this.mainAxisAlignment = mainAxisAlignment;
        return this;
    }

    @Override
    public Widget setPos(Pos2d pos) {
        return super.setPos(pos);
    }
}
