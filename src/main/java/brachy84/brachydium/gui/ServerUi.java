package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.UiHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerUi {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((server, player, handler, buf, responseSender) -> {
            Gui gui = UiHandler.getCurrentGui(player);
            ISyncedWidget syncedWidget = gui.findSyncedWidget(buf.readInt());
            if (syncedWidget != null) {
                syncedWidget.readData(buf);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.UI_CLOSE, ((server, player, handler, buf, responseSender) -> {
            Gui gui = UiHandler.getCurrentGui(player);
            gui.close();
            UiHandler.remove(player);
        }));
    }
}