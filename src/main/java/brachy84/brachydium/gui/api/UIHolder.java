package brachy84.brachydium.gui.api;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface UIHolder {

    boolean hasUI();

    @NotNull
    Gui createUi(PlayerEntity player);
}
