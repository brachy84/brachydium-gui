package brachy84.brachydium.gui.mixin;

import brachy84.brachydium.gui.internal.UiHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Shadow private Profiler profiler;

    @Inject(method = "tickWorlds", at = @At("TAIL"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        profiler.push("server brachydium-gui tick");
        UiHandler.tickGuis();
        profiler.pop();
    }
}
