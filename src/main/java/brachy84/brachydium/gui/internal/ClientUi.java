package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.internal.old.GuiHandledScreen;
import brachy84.brachydium.gui.internal.old.GuiScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientUi {

    public static void init() {
        ScreenRegistry.<GuiScreenHandler, GuiHandledScreen>register(GuiScreenHandler.MODULAR_SCREEN_HANDLER, (screenHandler, inv, title) -> {
            return new GuiHandledScreen(screenHandler, inv);
        });

        ClientPlayNetworking.registerGlobalReceiver(UIFactory.UI_SYNC_ID, (client, handler, buf, responseSender) -> UIFactory.SyncPacket.read(buf));

        ClientPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((client, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(buf);
            if(syncedWidget != null) {
                syncedWidget.readData(buf);
            }
        }));
    }

    @Nullable
    private static ISyncedWidget getSyncedWidget(PacketByteBuf buf) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return null;
        ScreenHandler sh = player.currentScreenHandler;
        if(sh instanceof GuiScreenHandler) {
            Gui gui = ((GuiScreenHandler) sh).getGui();
            if(gui != null) {
                return gui.findSyncedWidget(buf.readInt());
            }
        }
        return null;
    }
}
