package brachy84.brachydium.gui.internal.wrapper;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AstrarreSlot implements Slot<ItemKey> {

    private final net.minecraft.screen.slot.Slot mcSlot;
    private final Key.Object<ItemStack> type;

    public AstrarreSlot(net.minecraft.screen.slot.Slot mcSlot) {
        this.mcSlot = mcSlot;
        this.type = new ObjectKeyImpl<>(mcSlot.getStack());
        this.type.onApply(() -> mcSlot.setStack(type.get(null)));
    }

    public static AstrarreSlot of(Inventory inv, int index) {
        return new AstrarreSlot(new net.minecraft.screen.slot.Slot(inv, index, 0, 0));
    }

    @Override
    public ItemKey getKey(@Nullable Transaction transaction) {
        return ItemKey.of(type.get(transaction));
    }

    @Override
    public int getQuantity(@Nullable Transaction transaction) {
        return type.get(transaction).getCount();
    }

    @Override
    public boolean set(@Nullable Transaction transaction, ItemKey key, int quantity) {
        if(quantity <= 0) return false;
        quantity = Math.min(quantity, key.getMaxStackSize());
        type.set(transaction, key.createItemStack(quantity));
        return true;
    }
}
