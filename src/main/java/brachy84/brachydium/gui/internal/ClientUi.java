package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.ISyncedWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ClientUi implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(UIFactory.UI_SYNC_ID, (client, handler, buf, responseSender) -> UIFactory.SyncPacket.read(buf));

        ClientPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((client, handler, buf, responseSender) -> {
            Gui gui = UiHandler.getCurrentGui(client.player);
            ISyncedWidget syncedWidget = gui.findSyncedWidget(buf.readInt());
            if (syncedWidget != null) {
                syncedWidget.readData(buf);
            }
        }));
    }
}
