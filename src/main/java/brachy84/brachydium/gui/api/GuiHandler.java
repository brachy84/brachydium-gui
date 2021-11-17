package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.internal.GuiScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GuiHandler {

    /**
     * Returns the current active {@link Gui} for the player, null if there are none.
     * Side independent.
     *
     * @param player user to get Gui for
     * @return the active Gui
     */
    @Nullable
    public static Gui getCurrentGui(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            return SCREEN_MAP.get(player);
        return getCurrentGuiClient();
    }

    /**
     * Returns the current active {@link Gui} for the player, null if there are none.
     * Client side only.
     *
     * @return the active Gui
     */
    @Environment(EnvType.CLIENT)
    @Nullable
    public static Gui getCurrentGuiClient() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof GuiScreen)
            return ((GuiScreen) screen).getGui();
        return null;
    }

    //==================
    //  Forbidden part

    private static final Map<ServerPlayerEntity, Gui> SCREEN_MAP = new HashMap<>();

    /**
     * Should only be called internally
     */
    @ApiStatus.Internal
    public static void openGui(ServerPlayerEntity player, Gui gui) {
        SCREEN_MAP.put(player, gui);
        gui.init();
    }

    /**
     * Should only be called internally
     */
    @ApiStatus.Internal
    public static void remove(ServerPlayerEntity player) {
        SCREEN_MAP.remove(player);
    }


    /**
     * Should only be called internally
     */
    @ApiStatus.Internal
    public static void tickGuis() {
        SCREEN_MAP.values().forEach(Gui::tick);
    }
}
