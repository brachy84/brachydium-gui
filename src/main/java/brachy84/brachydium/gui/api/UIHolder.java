package brachy84.brachydium.gui.api;

import brachy84.brachydium.gui.internal.Gui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface UIHolder {

    boolean hasUI();

    @NotNull
    Identifier getUiId();

    @NotNull
    Gui createUi(PlayerEntity player);
}
