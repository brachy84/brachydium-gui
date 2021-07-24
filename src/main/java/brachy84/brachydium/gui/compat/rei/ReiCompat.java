package brachy84.brachydium.gui.compat.rei;

import brachy84.brachydium.gui.api.Interactable;
import brachy84.brachydium.gui.api.math.AABB;
import brachy84.brachydium.gui.api.math.Pos2d;
import brachy84.brachydium.gui.api.widgets.ItemSlotWidget;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.GuiScreen;
import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackProvider;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class ReiCompat implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new ReiGuiScreen());
        registry.registerDraggableStackVisitor(new CursorVisitor());
        registry.registerFocusedStack(new FocusedStackProvider() {
            public CompoundEventResult<EntryStack<?>> provide(Screen screen, Point mouse) {
                if (screen instanceof GuiScreen containerScreen) {
                    Interactable interactable = containerScreen.getHoveredInteractable(Pos2d.ofReiPoint(mouse));
                    if (interactable instanceof ItemSlotWidget itemSlot) {
                        return CompoundEventResult.interruptTrue(EntryStacks.of(itemSlot.getResource()));
                    }
                }
                return CompoundEventResult.pass();
            }

            public double getPriority() {
                return -10.0D;
            }
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
    }

    public static class CursorVisitor implements DraggableStackVisitor<GuiScreen> {

        @Override
        public <R extends Screen> boolean isHandingScreen(R screen) {
            return screen instanceof GuiScreen;
        }
    }

    public static class CursorProvider implements DraggableStackProvider<GuiScreen> {

        @Override
        public @Nullable DraggableStack getHoveredStack(DraggingContext<GuiScreen> context, double mouseX, double mouseY) {
            return null;
        }

        @Override
        public <R extends Screen> boolean isHandingScreen(R screen) {
            return screen instanceof GuiScreen;
        }
    }
}
