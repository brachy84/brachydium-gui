package brachy84.brachydium.gui.internal.wrapper;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemSlotWrapper extends SingleStackStorage {

    private final Supplier<ItemStack> stackSupplier;
    private final Consumer<ItemStack> stackConsumer;

    public ItemSlotWrapper(Inventory inv, int index) {
        this.stackSupplier = () -> inv.getStack(index);
        this.stackConsumer = stack -> inv.setStack(index, stack);
    }

    public ItemSlotWrapper(Supplier<ItemStack> stackSupplier, Consumer<ItemStack> stackConsumer) {
        this.stackSupplier = stackSupplier;
        this.stackConsumer = stackConsumer;
    }

    @Override
    public ItemStack getStack() {
        return stackSupplier.get();
    }

    @Override
    public void setStack(ItemStack stack) {
        stackConsumer.accept(stack);
    }
}
