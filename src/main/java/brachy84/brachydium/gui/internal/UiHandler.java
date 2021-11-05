package brachy84.brachydium.gui.internal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UiHandler {

    private static final Map<ServerPlayerEntity, Gui> SCREEN_MAP = new HashMap<>();

    public static void openGui(ServerPlayerEntity player, Gui gui) {
        SCREEN_MAP.put(player, gui);
        gui.init();
    }

    public static void remove(ServerPlayerEntity player) {
        SCREEN_MAP.remove(player);
    }

    @Nullable
    public static Gui getCurrentGui(PlayerEntity player) {
        if(player instanceof ServerPlayerEntity)
            return SCREEN_MAP.get(player);
        return getCurrentGuiClient();
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static Gui getCurrentGuiClient() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen instanceof GuiScreen)
            return ((GuiScreen) screen).getGui();
        return null;
    }

    @ApiStatus.Internal
    public static void tickGuis() {
        SCREEN_MAP.values().forEach(Gui::tick);
    }
}
