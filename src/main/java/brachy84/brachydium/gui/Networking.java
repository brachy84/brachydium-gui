package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.UiHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Networking {

    public static final Identifier WIDGET_UPDATE = BrachydiumGui.id("widget_update");
    public static final Identifier UI_CLOSE = BrachydiumGui.id("ui_close");

    public static void serverInit() {
        ServerPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((server, player, handler, buf, responseSender) -> {
            Gui gui = UiHandler.getCurrentGui(player);
            ISyncedWidget syncedWidget = gui.findSyncedWidget(buf.readInt());
            syncedWidget.readData(false, buf);
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.UI_CLOSE, ((server, player, handler, buf, responseSender) -> {
            Gui gui = UiHandler.getCurrentGui(player);
            gui.close();
            UiHandler.remove(player);
        }));
    }
}
