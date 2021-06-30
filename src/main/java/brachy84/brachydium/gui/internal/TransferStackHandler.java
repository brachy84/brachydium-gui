package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.api.ItemTransferTag;
import brachy84.brachydium.gui.api.WidgetTag;

import java.util.HashMap;
import java.util.Map;

public class TransferStackHandler {

    private static final Map<WidgetTag, WidgetTag[]> SLOT_TAGS = new HashMap<>();

    static {
        registerSlotTag(ItemTransferTag.INPUT, ItemTransferTag.PLAYER_INV, ItemTransferTag.HOTBAR);
        registerSlotTag(ItemTransferTag.OUTPUT, ItemTransferTag.PLAYER_INV, ItemTransferTag.HOTBAR);
        registerSlotTag(ItemTransferTag.HOTBAR, ItemTransferTag.INPUT, ItemTransferTag.PLAYER_INV);
        registerSlotTag(ItemTransferTag.PLAYER_INV, ItemTransferTag.INPUT, ItemTransferTag.HOTBAR);
    }

    /**
     * f.e. registerSlotTag(SlotTags.INPUT, SlotTags.PLAYER, SlotTags.HOTBAR)
     * when the transfering slot has a INPUT tag then it will try to insert into slot that has a PLAYER tag
     * if it was unsuccesfull, it will try to insert into slots that have HOTBAR tag
     *
     * @param tag Source slot
     * @param transferOrder target slots
     */
    public static void registerSlotTag(WidgetTag tag, WidgetTag... transferOrder) {
        SLOT_TAGS.put(tag, transferOrder);
    }

    public static WidgetTag[] getTargetOrder(WidgetTag tag) {
        return SLOT_TAGS.get(tag);
    }
}
