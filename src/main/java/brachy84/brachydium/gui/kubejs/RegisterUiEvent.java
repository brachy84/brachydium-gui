package brachy84.brachydium.gui.kubejs;

import brachy84.brachydium.gui.internal.Gui;
import dev.latvian.kubejs.server.ServerEventJS;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RegisterUiEvent extends ServerEventJS {

    public static final Event<Consumer<RegisterUiEvent>> EVENT;

    public void registerFor(String uiId, Gui gui) {
        Gui.registerGui(new Identifier(uiId), gui);
    }

    static {
        EVENT = EventFactory.createArrayBacked(Consumer.class,
                (listeners) -> (event) -> {
                    for (Consumer<RegisterUiEvent> listener : listeners) {
                        listener.accept(event);
                    }
                });
    }
}
