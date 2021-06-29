package brachy84.brachydium.gui;

import brachy84.brachydium.gui.api.ISyncedWidget;
import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.old.GuiScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ServerUi {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Networking.WIDGET_UPDATE, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget != null) {
                syncedWidget.readData(buf);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_CLICKED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Pos2d pos = new Pos2d(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                boolean doubleClick = buf.readBoolean();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onClick(pos, button, doubleClick);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_RELEASED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Pos2d pos = new Pos2d(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onClickReleased(pos, button);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_DRAGGED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Pos2d pos = new Pos2d(buf.readDouble(), buf.readDouble());
                int button = buf.readInt();
                double deltaX = buf.readDouble(), deltaY = buf.readDouble();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onMouseDragged(pos, button, deltaX, deltaY);
                });
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Networking.MOUSE_SCROLLED, ((server, player, handler, buf, responseSender) -> {
            ISyncedWidget syncedWidget = getSyncedWidget(player, buf);
            if(syncedWidget instanceof Interactable) {
                Pos2d pos = new Pos2d(buf.readDouble(), buf.readDouble());
                double amount = buf.readDouble();
                server.execute(() -> {
                    ((Interactable) syncedWidget).onScrolled(pos, amount);
                });
            }
        }));
    }

    @Nullable
    private static ISyncedWidget getSyncedWidget(ServerPlayerEntity player, PacketByteBuf buf) {
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
