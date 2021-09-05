package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.BrachydiumGui;
import brachy84.brachydium.gui.api.UIHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

public abstract class UIFactory<T extends UIHolder> {

    public static final Registry<UIFactory> REGISTRY = FabricRegistryBuilder.createSimple(UIFactory.class, BrachydiumGui.id("ui_factories")).buildAndRegister();

    public static void register(UIFactory<?> factory) {
        Registry.register(REGISTRY, factory.getId(), factory);
    }

    public static final Identifier UI_SYNC_ID = BrachydiumGui.id("modular_gui");

    protected UIFactory() {
    }

    public boolean openUI(T uiHolder, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            return openUI(uiHolder, (ServerPlayerEntity) player);
        return uiHolder.hasUI();
    }

    public final boolean openUI(T uiHolder, ServerPlayerEntity player) {
        if (!uiHolder.hasUI()) return false;
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
    protected final void openClientUi(UIHolder uiHolder) {
        Gui gui = uiHolder.createUi(MinecraftClient.getInstance().player);
        MinecraftClient.getInstance().setScreen(new GuiScreen(gui));
    }

    public abstract Identifier getId();

    @Environment(EnvType.CLIENT)
    public abstract T readHolderFromSyncData(PacketByteBuf syncData);

    public abstract void writeHolderToSyncData(PacketByteBuf syncData, T holder);

    public static class SyncPacket {

        public static void read(PacketByteBuf buf) {
            Identifier factoryId = buf.readIdentifier();
            UIFactory<?> factory = REGISTRY.get(factoryId);//UiFactoryRegistry.tryGetFactory(factoryId);
            if (factory != null) {
                UIHolder holder = factory.readHolderFromSyncData(buf);
                MinecraftClient.getInstance().execute(() -> {
                    factory.openClientUi(holder);
                });
            }
        }
    }
}
