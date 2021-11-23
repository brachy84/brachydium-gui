package brachy84.brachydium.gui.internal;

import brachy84.brachydium.gui.Networking;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.api.GuiHandler;
import brachy84.brachydium.gui.api.UIFactory;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.api.helpers.ISyncedWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientUi implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(UIFactory.UI_SYNC_ID, (client, handler, buf, responseSender) -> {
            Identifier factoryId = buf.readIdentifier();
            UIFactory<?> factory = UIFactory.REGISTRY.get(factoryId);
            if (factory == null) {
                throw new NullPointerException("Could not get UIFactory from Registry. Either the factory was not registered or the id used to register doesn't match UIFactory#getId()");
            }
            UIHolder holder = factory.readHolderFromSyncData(buf);
            if(holder == null) {
                throw new NullPointerException("Could not read UIHolder from UIFactory.");
            }
            MinecraftClient.getInstance().execute(() -> {
                Gui gui = holder.createUi(MinecraftClient.getInstance().player);
                MinecraftClient.getInstance().setScreen(new GuiScreen(gui));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((client, handler, buf, responseSender) -> {
            Gui gui = GuiHandler.getCurrentGuiClient();
            if(gui.isInitialised()) {
                ISyncedWidget syncedWidget = gui.findSyncedWidget(buf.readVarInt());
                syncedWidget.readServerData(buf.readVarInt(), buf);
            }
        }));
    }
}
