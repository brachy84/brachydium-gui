package brachy84.brachydium.gui.api.helpers;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.GuiHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Implement this to let them synchronize data between server and client
 * see also: {@link Interactable}
 */
public interface ISyncedWidget {

    @ApiStatus.OverrideOnly
    @Environment(EnvType.CLIENT)
    void readServerData(int id, PacketByteBuf buf);

    @ApiStatus.OverrideOnly
    void readClientData(int id, PacketByteBuf buf);

    /**
     * Sends the written data to {@link #readClientData(int, PacketByteBuf)}
     *
     * @param id         helper to determine the type
     * @param bufBuilder data builder
     */
    @ApiStatus.NonExtendable
    @Environment(EnvType.CLIENT)
    default void syncToServer(int id, Consumer<PacketByteBuf> bufBuilder) {
        PacketByteBuf buf = PacketByteBufs.create();
        Gui gui = GuiHandler.getCurrentGuiClient();
        if (gui == null)
            throw new NullPointerException("Could not find Gui for player");
        buf.writeVarInt(gui.findIdForSyncedWidget(this));
        buf.writeVarInt(id);
        bufBuilder.accept(buf);
        ClientPlayNetworking.send(Networking.WIDGET_UPDATE, buf);
    }

    /**
     * Sends the written data to {@link #readServerData(int, PacketByteBuf)}
     *
     * @param player     player to send data to. Usually just getGui().player
     * @param id         helper to determine the type
     * @param bufBuilder data builder
     */
    @ApiStatus.NonExtendable
    default void syncToClient(PlayerEntity player, int id, Consumer<PacketByteBuf> bufBuilder) {
        if (!(player instanceof ServerPlayerEntity))
            throw new IllegalArgumentException("Can not send data from client to client");
        PacketByteBuf buf = PacketByteBufs.create();
        Gui gui = GuiHandler.getCurrentGui(player);
        if (gui == null)
            throw new NullPointerException("Could not find Gui for player");
        buf.writeVarInt(gui.findIdForSyncedWidget(this));
        buf.writeVarInt(id);
        bufBuilder.accept(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) player, Networking.WIDGET_UPDATE, buf);
    }
}
