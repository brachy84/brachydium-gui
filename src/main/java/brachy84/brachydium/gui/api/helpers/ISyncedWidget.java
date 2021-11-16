package brachy84.brachydium.gui.api.helpers;

import brachy84.brachydium.gui.Networking;
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
    void readData(boolean fromServer, PacketByteBuf data);

    /**
     * This methods handles data writing
     * You should override but not not call this
     *
     * @param data to write to
     */
    @ApiStatus.OverrideOnly
    void writeData(boolean fromServer, PacketByteBuf data);

    /**
     * Use this method to send the data
     * You don't need to override it, just call
     *
     * @param player to send data to
     */
    default void sendToClient(PlayerEntity player) {
        if(!(player instanceof ServerPlayerEntity))
            throw new IllegalArgumentException("Can't send to client from client");
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(GuiHandler.getCurrentGui(player).findIdForSyncedWidget(this));
        writeData(true, buf);
        ServerPlayNetworking.send((ServerPlayerEntity) player, Networking.WIDGET_UPDATE, buf);
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
            buf.writeInt(GuiHandler.getCurrentGuiClient().findIdForSyncedWidget(this));
            writeData(false, buf);
            ClientPlayNetworking.send(Networking.WIDGET_UPDATE, buf);
        }
    }
}
