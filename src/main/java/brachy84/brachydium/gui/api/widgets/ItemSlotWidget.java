package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.api.GuiHelper;
import brachy84.brachydium.gui.internal.wrapper.IModifiableStorage;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemSlotWidget extends ResourceSlotWidget<ItemStack> {

    public static final Size SIZE = new Size(18, 18);

    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private Predicate<ItemStack> canInsert;
    private ItemTransferTag tag;
    private int mark;

    public ItemSlotWidget(IModifiableStorage<ItemVariant> itemSlot, Pos2d pos) {
        this(() -> itemSlot.getResource().toStack((int) itemSlot.getAmount()),
                stack -> itemSlot.setResource(ItemVariant.of(stack), stack.getCount()),
                pos);
        setInsertPredicate(stack -> itemSlot.canInsert(ItemVariant.of(stack)));
    }

    public ItemSlotWidget(Inventory inv, int index, Pos2d pos) {
        this(() -> inv.getStack(index), stack -> inv.setStack(index, stack), pos);
        setInsertPredicate(stack -> inv.isValid(index, stack));
    }

    public ItemSlotWidget(Slot slot, Pos2d pos) {
        this(slot::getStack, slot::setStack, pos);
        setInsertPredicate(slot::canInsert);
    }

    public ItemSlotWidget(Supplier<ItemStack> getter, Consumer<ItemStack> setter, Pos2d pos) {
        this.setter = setter;
        this.getter = getter;
        this.mark = 0;
        setSize(SIZE);
        setPos(pos);
        setInsertPredicate(stack -> true);
    }

    @Override
    public void readData(boolean fromServer, PacketByteBuf data) {
        setResource(data.readItemStack());
    }

    @Override
    public void writeData(boolean fromServer, PacketByteBuf data) {
        data.writeItemStack(getResource());
    }

    @Override
    public void renderResource(MatrixStack matrices, Pos2d mousePos) {
        GuiHelper.drawItem(getResource(), getPos().add(1, 1f));
    }

    @Override
    public void renderTooltip(MatrixStack matrices, Pos2d mousePos, float delta) {
        getGui().getScreen().renderTooltip(matrices, getGui().getScreen().getTooltipFromItem(getResource()), getResource().getTooltipData(), (int) mousePos.x, (int) mousePos.y);
    }

    @Override
    public ItemStack getResource() {
        return getter.get();
    }

    @Override
    public boolean setResource(ItemStack resource) {
        setter.accept(resource);
        return true;
    }

    public int insert(ItemStack stack) {
        ItemStack origin = getResource();
        if (!isEmpty() && (!ItemStack.areItemsEqual(stack, origin) || !ItemStack.areNbtEqual(stack, origin)))
            return 0;
        int toInsert = Math.min(stack.getCount(), origin.getMaxCount() - origin.getCount());
        if(toInsert == 0) return 0;
        stack.setCount(origin.getCount() + toInsert);
        setResource(stack, Action.PUT);
        return toInsert;
    }

    @Override
    public boolean canPut(ItemStack resource, PlayerEntity player) {
        return canInsert.test(resource);
    }

    public void setCursorStack(ItemStack stack) {
        getGui().setCursorStack(stack);
    }

    public ItemStack getCursorStack() {
        return getGui().getCursorStack();
    }

    @Override
    public boolean isEmpty() {
        return getResource().isEmpty();
    }

    @Override
    public TextureArea getFallbackTexture() {
        return TextureArea.fullImage("brachydium", "gui/base/slot");
    }

    private ItemStack newStack(ItemStack stack, int amount) {
        ItemStack stack1 = stack.copy();
        stack1.setCount(amount);
        return stack1;
    }

    @Override
    public ItemSlotWidget addTag(WidgetTag tag) {
        if (tag instanceof ItemTransferTag) {
            this.tag = (ItemTransferTag) tag;
        }
        super.addTag(tag);
        return this;
    }

    public ItemSlotWidget markInput() {
        this.mark = 1;
        if (this.tag == null)
            addTag(ItemTransferTag.INPUT);
        return this;
    }

    public ItemSlotWidget markOutput() {
        this.mark = 2;
        if (this.tag == null)
            addTag(ItemTransferTag.OUTPUT);
        return this;
    }

    public ItemSlotWidget setInsertPredicate(Predicate<ItemStack> insertPredicate) {
        this.canInsert = Objects.requireNonNull(insertPredicate);
        return this;
    }

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        ItemStack cursorStack = getCursorStack();
        ItemStack slotStack = getResource();
        // Left click
        if (cursorStack.isEmpty() && slotStack.isEmpty()) {
            return ActionResult.PASS;
        }
        if (buttonId == 0) {
            if (Interactable.hasShiftDown()) {
                transferStack();
            } else if (cursorStack.isEmpty()) {
                if (setResource(ItemStack.EMPTY, Action.TAKE))
                    setCursorStack(slotStack.copy());
            } else if (cursorStack.getItem() == slotStack.getItem() && ItemStack.areNbtEqual(cursorStack, slotStack)) {
                int cursorAmount = cursorStack.getCount();
                int slotAmount = slotStack.getCount();
                int moved = Math.min(cursorAmount, slotStack.getItem().getMaxCount() - slotAmount);
                if (setResource(newStack(slotStack, slotAmount + moved), Action.PUT))
                    setCursorStack(newStack(cursorStack, cursorAmount - moved));
            } else if (slotStack.isEmpty()) {
                if (setResource(cursorStack.copy(), Action.PUT))
                    setCursorStack(ItemStack.EMPTY);
            } else {
                setResource(cursorStack.copy());
                setCursorStack(slotStack.copy());
            }
            // Right click
        } else if (buttonId == 1) {
            if (cursorStack.isEmpty()) {
                int taken = slotStack.getCount() / 2;
                //slotStack.setCount(slotStack.getCount() - taken);
                if (setResource(newStack(slotStack, slotStack.getCount() - taken), Action.TAKE))
                    setCursorStack(newStack(slotStack, taken));
            } else if (slotStack.isEmpty()) {
                if (setResource(new ItemStack(cursorStack.getItem()), Action.PUT))
                    setCursorStack(newStack(cursorStack, cursorStack.getCount() - 1));
            } else if (slotStack.getItem() == cursorStack.getItem() && ItemStack.areNbtEqual(cursorStack, slotStack)) {
                if (slotStack.getItem().getMaxCount() - slotStack.getCount() >= 1) {
                    if (setResource(newStack(slotStack, slotStack.getCount() + 1), Action.PUT))
                        setCursorStack(newStack(cursorStack, cursorStack.getCount() - 1));
                }
            }
            // Scroll click
        } else if (buttonId == 2) {
            if (getGui().player.isCreative() && !slotStack.isEmpty()) {
                setCursorStack(newStack(slotStack, slotStack.getMaxCount()));
            }
        }
        // lastly simply sync the slot to the server
        if (getGui().player instanceof ClientPlayerEntity) {
            sendToServer();
        }
        return ActionResult.SUCCESS;
    }

    protected void transferStack() {
        if (tag == null) return;
        ItemStack stack = getResource().copy();
        if (!canTake(getGui().player)) return;
        WidgetTag[] order = TransferStackHandler.getTargetOrder(tag);
        List<ItemSlotWidget> itemSlots = getGui().getMatchingSyncedWidgets(widget -> widget instanceof ItemSlotWidget);
        int toInsert = stack.getCount();
        outer:
        for (WidgetTag tag : order) {
            for (ItemSlotWidget slot : itemSlots.stream().filter(widget -> widget.tag == tag).collect(Collectors.toList())) {
                if (slot == this) continue;
                stack.setCount(toInsert);
                toInsert -= slot.insert(stack);
                if (toInsert == 0)
                    break outer;
            }
        }
        if (toInsert == 0) {
            setResource(ItemStack.EMPTY);
        } else {
            setResource(newStack(stack, toInsert));
        }
    }

    @Override
    public List<Widget> getReiWidgets(AABB bounds, Pos2d reiPos) {
        List<Widget> widgets = new ArrayList<>();
        me.shedaniel.rei.api.client.gui.widgets.Slot slot = Widgets.createSlot(reiPos.add(new Pos2d(1, 1)).asReiPoint());
        slot.backgroundEnabled(false);
        if (tag == ItemTransferTag.INPUT || mark == 1) {
            slot.markInput();
        } else if (tag == ItemTransferTag.OUTPUT || mark == 2) {
            slot.markOutput();
        }
        widgets.add(slot);
        Widget render = Widgets.createDrawableWidget(((helper, matrices, mouseX, mouseY, delta) -> {
            if (getTextures().size() > 0) {
                for (ITexture drawable : getTextures()) {
                    GuiHelper.drawTexture(matrices, drawable, reiPos, getSize());
                }
            } else {
                GuiHelper.drawTexture(matrices, getFallbackTexture(), reiPos, getSize());
            }
        }));
        widgets.add(render);
        return widgets;
    }
}
