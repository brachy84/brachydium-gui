package brachy84.brachydium.gui;

import brachy84.brachydium.gui.internal.UIFactory;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UiFactoryRegistry {

    private static final BiMap<Identifier, UIFactory<?>> FACTORIES = HashBiMap.create(0);

    public static void register(Identifier id, UIFactory<?> factory) {
        FACTORIES.forcePut(id, factory);
    }

    @Nullable
    public static UIFactory<?> tryGet(Identifier id) {
        return FACTORIES.get(id);
    }

    @Nullable
    public static Identifier tryGet(UIFactory<?> id) {
        return FACTORIES.inverse().get(id);
    }
}
