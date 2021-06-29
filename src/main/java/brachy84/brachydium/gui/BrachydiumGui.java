package brachy84.brachydium.gui;

import brachy84.brachydium.gui.internal.Widget;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.Reflection;

public class BrachydiumGui implements ModInitializer {

    public static final String MODID = "brachydiumgui";
    public static final String NAME = "Brachydium Gui";

    public static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger(MODID);
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
    }
}
