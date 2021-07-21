package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.BrachydiumGui;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.UiFactoryRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class UIFactory<T extends UIHolder> {

    public static final Identifier UI_SYNC_ID = BrachydiumGui.id("modular_gui");
    public static final Map<Integer, UIHolder> holderCache = new HashMap<>();

    public static UIHolder getCachedHolder(int syncId) {
        return holderCache.get(syncId);
    }

    public final Identifier id;

    protected UIFactory(Identifier id) {
        this.id = id;
    }

    public final boolean openUI(T uiHolder, ServerPlayerEntity player) {
        if(!uiHolder.hasUI()) return false;
        BrachydiumGui.LOGGER.info("Building UI");

        UiHandler.openGui(player, uiHolder.createUi(player));

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(getId());
        writeHolderToSyncData(buf, uiHolder);

        ServerPlayNetworking.send(player, UI_SYNC_ID, buf);
        return true;
    }

    @ApiStatus.Internal
    @Environment(EnvType.CLIENT)
    public final void openClientUi(UIHolder uiHolder) {
        Gui gui = uiHolder.createUi(MinecraftClient.getInstance().player);
        MinecraftClient.getInstance().setScreen(new GuiScreen(gui));
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
                MinecraftClient.getInstance().execute(() -> {
                    factory.openClientUi(holder);
                });
            }
        }
    }
}
