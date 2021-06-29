package brachy84.brachydium.gui.internal.old;

import brachy84.brachydium.gui.BrachydiumGui;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.UiFactoryRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public abstract class UIFactory<T extends UIHolder> {

    public static final Identifier UI_SYNC_ID = BrachydiumGui.id("modular_gui");
    public static final Map<Integer, UIHolder> holderCache = new HashMap<>();

    public static UIHolder getCachedHolder(int syncId) {
        return holderCache.get(syncId);
    }

    public final Identifier id;

    public UIFactory(Identifier id) {
        this.id = id;
    }

    public final void openUI(T uiHolder, ServerPlayerEntity player) {
        if(!uiHolder.hasUI()) return;
        BrachydiumGui.LOGGER.info("Building UI");

        OptionalInt optionalInt = player.openHandledScreen(GuiScreenHandler.createFactory(uiHolder));
        if(optionalInt.isPresent()) {
            int syncId = optionalInt.getAsInt();
            holderCache.put(syncId, uiHolder);
        }
    }

    @Environment(EnvType.CLIENT)
    public final void openClientUi(UIHolder uiHolder, int syncId) {
    }

    public Identifier getId() {
        return id;
    }

    @Environment(EnvType.CLIENT)
    public abstract T readHolderFromSyncData(PacketByteBuf syncData);

    public abstract void writeHolderToSyncData(PacketByteBuf syncData, T holder);

    public static class SyncPacket {

        public static void read(PacketByteBuf buf) {
            Identifier factoryId = buf.readIdentifier();
            UIFactory<?> factory = UiFactoryRegistry.tryGet(factoryId);
            if (factory != null) {
                UIHolder holder = factory.readHolderFromSyncData(buf);
                factory.openClientUi(holder, buf.readInt());
            }
        }
    }
}
