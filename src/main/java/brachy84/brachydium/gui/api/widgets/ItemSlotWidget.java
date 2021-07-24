package brachy84.brachydium.gui.api.widgets;

import brachy84.brachydium.gui.api.*;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.math.Size;
import brachy84.brachydium.gui.internal.TransferStackHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.stream.Collectors;

public class ItemSlotWidget extends ResourceSlotWidget<ItemStack> implements Draggable {

    public static final Size SIZE = new Size(18, 18);

    private final Inventory inv;
    private final int index;
    private ItemTransferTag tag;

    public ItemSlotWidget(Inventory inv, int index, Pos2d pos) {
        setSize(SIZE);
        setPos(pos);
        this.inv = inv;
        this.index = index;
        this.dragState = State.IDLE;
    }

    @Override
    public void readData(PacketByteBuf data) {
        setResource(data.readItemStack());
    }

    @Override
    public void writeData(PacketByteBuf data) {
        data.writeItemStack(getResource());
    }

    @Override
    public boolean isMouseOver(Pos2d pos) {
        return getBounds().isInBounds(pos);
    }

    @Override
    public void renderResource(IGuiHelper helper, MatrixStack matrices) {
        helper.drawItem(matrices, getResource(), getPos().add(1, 1));
    }

    @Override
    public void renderTooltip(IGuiHelper helper, MatrixStack matrices, float delta) {
        getGui().getScreen().renderTooltip(matrices, getGui().getScreen().getTooltipFromItem(getResource()), getResource().getTooltipData(), (int) helper.getMousePos().x, (int) helper.getMousePos().y);
    }

    @Override
    public ItemStack getResource() {
        return inv.getStack(index);
    }

    @Override
    public boolean setResource(ItemStack resource) {
        inv.setStack(index, resource);
        return true;
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

    @Override
    public ActionResult onClick(Pos2d pos, int buttonId, boolean isDoubleClick) {
        ItemStack cursorStack = getCursorStack();
        ItemStack slotStack = getResource();
        // Left click
        if(cursorStack.isEmpty() && slotStack.isEmpty()) {
            return ActionResult.PASS;
        }
        if (buttonId == 0) {
            if (Interactable.hasShiftDown()) {
                transferStack();
            } else if (cursorStack.isEmpty()) {
                if (setResource(ItemStack.EMPTY, Action.TAKE))
                    setCursorStack(slotStack.copy());
            } else if (cursorStack.getItem() == slotStack.getItem()) {
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
            } else if (slotStack.getItem() == cursorStack.getItem()) {
                if (slotStack.getItem().getMaxCount() - slotStack.getCount() >= 1) {
                    if (setResource(newStack(slotStack, slotStack.getCount() + 1), Action.PUT))
                        setCursorStack(newStack(cursorStack, cursorStack.getCount() - 1));
                }
            }
            // Scroll click
        } else if (buttonId == 2) {
            if (getGui().player.isCreative() && !slotStack.isEmpty()) {
                setCursorStack(new ItemStack(slotStack.getItem(), slotStack.getMaxCount()));
            }
        }
        // lastly simply sync the slot and the cursor slot to the client
        if (getGui().player instanceof ClientPlayerEntity) {
            sendToServer();
            getGui().getCursor().sendToServer();
        }
        return ActionResult.SUCCESS;
    }

    protected void transferStack() {
        if (tag == null) return;
        ItemStack stack = getResource().copy();
        if (!canTake(getGui().player)) return;
        WidgetTag[] order = TransferStackHandler.getTargetOrder(tag);
        List<ItemSlotWidget> itemSlots = getGui().getMatchingSyncedWidgets(widget -> widget instanceof ItemSlotWidget);
        outer:
        for (WidgetTag tag : order) {
            for (ItemSlotWidget slot : itemSlots.stream().filter(widget -> widget.tag == tag).collect(Collectors.toList())) {
                if (slot == this) continue;
                Slot mcSlot = new Slot(slot.inv, slot.index, 0, 0);
                stack = mcSlot.insertStack(stack);
                if (stack.getCount() == 0)
                    break outer;
            }
        }
        if (stack.getCount() == 0) {
            setResource(ItemStack.EMPTY);
        } else {
            setResource(newStack(stack, stack.getCount()));
        }
    }

    private State dragState;

    @Override
    public void renderMovingState(IGuiHelper helper, MatrixStack matrices, float delta) {
        render(helper, matrices, delta);
    }

    @Override
    public boolean onDragStart(int button) {
        return true;
    }

    @Override
    public void onDragEnd(boolean successful) {
    }

    @Override
    public State getState() {
        return dragState;
    }

    @Override
    public void setState(State state) {
        this.dragState = state;
    }
}
