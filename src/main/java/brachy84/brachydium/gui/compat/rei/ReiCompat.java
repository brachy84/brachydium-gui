package brachy84.brachydium.gui.compat.rei;

import brachy84.brachydium.gui.api.helpers.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.api.Gui;
import brachy84.brachydium.gui.internal.GuiScreen;
import brachy84.brachydium.gui.api.GuiHandler;
import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.config.DisplayPanelLocation;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;

public class ReiCompat implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new ReiGuiScreen());
        registry.registerFocusedStack((screen, mouse) -> {
            if (screen instanceof GuiScreen guiScreen) {
                Interactable interactable = guiScreen.getHoveredInteractable(Pos2d.ofReiPoint(mouse));
                if (interactable instanceof ItemSlotWidget slotWidget) {
                    return CompoundEventResult.interruptTrue(EntryStacks.of(slotWidget.getResource()));
                }
            }
            return CompoundEventResult.pass();
        });
    }

    public static class ReiGuiScreen implements DisplayBoundsProvider<GuiScreen> {

        @Override
        public Rectangle getScreenBounds(GuiScreen screen) {
            if (screen != null)
                return screen.getGui().getBounds().asReiRectangle();
            return AABB.of(Gui.getScreenSize(), Pos2d.ZERO).asReiRectangle();
        }

        @Override
        public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
            return screen.isAssignableFrom(GuiScreen.class);
        }

        @Override
        public ActionResult shouldScreenBeOverlaid(Class<?> screen) {
            return screen.isAssignableFrom(GuiScreen.class) ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        @Override
        public boolean shouldRecalculateArea(DisplayPanelLocation location, Rectangle rectangle) {
            Gui gui = GuiHandler.getCurrentGuiClient();
            if (gui == null) return false;
            return AABB.ofReiRectangle(rectangle).intersects(gui.getBounds());
        }
    }
}
