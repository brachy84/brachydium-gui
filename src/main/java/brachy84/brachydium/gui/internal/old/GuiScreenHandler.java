package brachy84.brachydium.gui.internal.old;

import brachy84.brachydium.gui.BrachydiumGui;
import brachy84.brachydium.gui.api.UIHolder;
import brachy84.brachydium.gui.internal.Gui;
import brachy84.brachydium.gui.internal.UIFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class GuiScreenHandler extends ScreenHandler {

    public final static ScreenHandlerType<GuiScreenHandler> MODULAR_SCREEN_HANDLER;

    static {
        MODULAR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(BrachydiumGui.id("shitty_ass_screen_handler_type_shit_crack_nightmare"), (syncId, inv) -> {
            return new GuiScreenHandler(syncId, UIFactory.getCachedHolder(syncId), inv.player);
        });
    }

    public static NamedScreenHandlerFactory createFactory(UIHolder uiHolder) {
        return new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() { return new LiteralText(""); }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new GuiScreenHandler(syncId, uiHolder, player);
            }
        };
    }

    private UIHolder uiHolder;
    private PlayerEntity player;
    private Gui gui;

    protected GuiScreenHandler(int syncId, UIHolder uiHolder, PlayerEntity player) {
        super(MODULAR_SCREEN_HANDLER, syncId);
        if(uiHolder == null) {
            throw new NullPointerException("UIHolder can't be null");
        }
        this.uiHolder = uiHolder;
        this.gui = uiHolder.createUi(player);

        gui.init();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public Gui getGui() {
        return gui;
    }

    public UIHolder getUiHolder() {
        return uiHolder;
    }
}
