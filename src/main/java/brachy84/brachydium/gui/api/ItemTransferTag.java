package brachy84.brachydium.gui.api;

import java.util.function.Predicate;

public class ItemTransferTag extends WidgetTag {

    /**
     * Used for the slots in a player inventory
     */
    public static final ItemTransferTag PLAYER_INV = new ItemTransferTag();
    /**
     * Use for player hotbar slots
     */
    public static final ItemTransferTag HOTBAR = new ItemTransferTag();
    /**
     * Use for machine input slots
     */
    public static final ItemTransferTag INPUT = new ItemTransferTag();
    /**
     * Use for machine output slots
     */
    public static final ItemTransferTag OUTPUT = new ItemTransferTag();
    /**
     * Use for storage slot f.e. chests
     */
    public static final ItemTransferTag STORAGE = new ItemTransferTag();

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
