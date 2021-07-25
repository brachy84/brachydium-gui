package brachy84.brachydium.gui;

import brachy84.brachydium.gui.internal.UIFactory;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UiFactoryRegistry {

    private static final BiMap<Identifier, UIFactory<?>> FACTORIES = HashBiMap.create(0);

    public static void register(UIFactory<?> factory) {
        Objects.requireNonNull(factory, "UIFactory is null during registry");
        Objects.requireNonNull(factory.getId(), "UIFactory ID is null during registry");
        if(FACTORIES.containsKey(factory.getId()))
            throw new IllegalStateException("Can't register factory {} since it already exists!");
        FACTORIES.forcePut(factory.getId(), factory);
    }

    @Nullable
    public static UIFactory<?> tryGetFactory(Identifier id) {
        return FACTORIES.get(id);
    }

    @Nullable
    public static Identifier tryGetId(UIFactory<?> id) {
        return FACTORIES.inverse().get(id);
    }
}
