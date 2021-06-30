package brachy84.brachydium.gui.internal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UiHandler {

    private static final Map<ServerPlayerEntity, Gui> SCREEN_MAP = new HashMap<>();

    public static void openGui(ServerPlayerEntity player, Gui gui) {
        SCREEN_MAP.put(player, gui);
    }

    public static void remove(ServerPlayerEntity player) {
        SCREEN_MAP.remove(player);
    }

    @Nullable
    public static Gui getCurrentGui(ServerPlayerEntity player) {
        return SCREEN_MAP.get(player);
    }

    @Nullable
    public static Gui getCurrentGui(ClientPlayerEntity player) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen instanceof GuiScreen guiScreen)
            return guiScreen.getGui();
        return null;
    }
}