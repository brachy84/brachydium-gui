package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.internal.UiHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implement this to let them synchronize data between server and client
 * see also: {@link Interactable}
 */
public interface ISyncedWidget {

    /**
     * This methods handles data reading
     * You should override but not not call this
     *
     * @param data to read
     */
    @ApiStatus.OverrideOnly
    void readData(PacketByteBuf data);

    /**
     * This methods handles data writing
     * You should override but not not call this
     *
     * @param data to write to
     */
    @ApiStatus.OverrideOnly
    void writeData(PacketByteBuf data);

    /**
     * Use this method to send the data
     * You don't need to override it, just call
     *
     * @param player to send data to
     */
    default void sendToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(UiHandler.getCurrentGui(player).findIdForSyncedWidget(this));
        writeData(buf);
        ServerPlayNetworking.send(player, Networking.WIDGET_UPDATE, buf);
    }

    /**
     * Use this method to send the data
     * You don't need to override it, just call
     */
    @Environment(EnvType.CLIENT)
    default void sendToServer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(UiHandler.getCurrentGuiClient().findIdForSyncedWidget(this));
            writeData(buf);
            ClientPlayNetworking.send(Networking.WIDGET_UPDATE, buf);
        }
    }
}
