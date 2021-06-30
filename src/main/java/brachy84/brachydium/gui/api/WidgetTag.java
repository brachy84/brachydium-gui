package brachy84.brachydium.gui.api;

import java.util.function.Predicate;

public class WidgetTag {

    public WidgetTag() {
    }

    public static WidgetTag create(Predicate<WidgetTag> compatibleWith) {
        return new WidgetTag() {
            @Override
            public Predicate<WidgetTag> getCompatPredicate() {
                return compatibleWith;
            }
        };
    }

    public Predicate<WidgetTag> getCompatPredicate() {
        return tag -> true;
    }
}
