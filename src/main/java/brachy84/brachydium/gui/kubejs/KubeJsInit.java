package brachy84.brachydium.gui.kubejs;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.script.ScriptType;

public class KubeJsInit implements KubeJSInitializer {

    @Override
    public void onKubeJSInitialization() {
        RegisterUiEvent.EVENT.register((event) -> {
            event.post(ScriptType.SERVER, "brachydium.gui");
        });
    }
}
