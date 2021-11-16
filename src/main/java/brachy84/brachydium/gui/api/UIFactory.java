package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.BrachydiumGui;
import brachy84.brachydium.gui.internal.GuiScreen;
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

    /**
     * UIFactory Registry
     */
    public static final Registry<UIFactory> REGISTRY = FabricRegistryBuilder.createSimple(UIFactory.class, BrachydiumGui.id("ui_factories")).buildAndRegister();

    /**
     * Registers a UIFactory
     * @param factory factory to register
     */
    public static void register(UIFactory<?> factory) {
        Registry.register(REGISTRY, factory.getId(), factory);
    }

    /**
     * Only used internally for syncing
     */
    public static final Identifier UI_SYNC_ID = BrachydiumGui.id("modular_gui");

    /**
     * UIFactory should only be instantiated once
     */
    protected UIFactory() {
    }

    /**
     * Opens a {@link Gui} and syncs it to client;
     *
     * @param uiHolder the gui holder
     * @param player the player who opens the gui
     * @return if the gui was successfully opened
     */
    public final boolean openUI(T uiHolder, PlayerEntity player) {
        if(!(player instanceof ServerPlayerEntity)) {
            throw new IllegalArgumentException("UIFactory#openUI() should only be called from the server!");
        }
        if (!uiHolder.hasUI()) return false;
        BrachydiumGui.LOGGER.info("Building UI");
        GuiHandler.openGui((ServerPlayerEntity) player, uiHolder.createUi(player));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(getId());
        writeHolderToSyncData(buf, uiHolder);
        ServerPlayNetworking.send((ServerPlayerEntity) player, UI_SYNC_ID, buf);
        return true;
    }

    public abstract Identifier getId();

    @Environment(EnvType.CLIENT)
    public abstract T readHolderFromSyncData(PacketByteBuf syncData);

    public abstract void writeHolderToSyncData(PacketByteBuf syncData, T holder);
}
