package brachy84.brachydium.gui.api;

import java.util.function.Predicate;

public class ItemTransferTag extends WidgetTag {

    public static final ItemTransferTag PLAYER_INV = new ItemTransferTag();
    public static final ItemTransferTag HOTBAR = new ItemTransferTag();
    public static final ItemTransferTag INPUT = new ItemTransferTag();
    public static final ItemTransferTag OUTPUT = new ItemTransferTag();

    public static ItemTransferTag create(Predicate<WidgetTag> compatibleWith) {
        return new ItemTransferTag() {
            @Override
            public Predicate<WidgetTag> getCompatPredicate() {
                return tag -> this.getCompatPredicate().test(tag) && compatibleWith.test(tag);
            }
        };
    }

    @Override
    public Predicate<WidgetTag> getCompatPredicate() {
        return tag -> !(tag instanceof ItemTransferTag);
    }
}
