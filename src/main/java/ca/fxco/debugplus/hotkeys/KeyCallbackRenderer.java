package ca.fxco.debugplus.hotkeys;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;

public class KeyCallbackRenderer extends KeyCallbackToggleBooleanConfigWithMessage {
    public KeyCallbackRenderer(IConfigBoolean config)
    {
        super(config);
    }

    /*@Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null && super.onKeyAction(action, key)) {
            if (!this.config.getBooleanValue()) return true;

            Entity entity = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
            String green = GuiBase.TXT_GREEN;
            String rst = GuiBase.TXT_RST;
            String strStatus = green + StringUtils.translate("malilib.message.value.on") + rst;

            if (key == RendererToggles.DEBUG_SHAPE_UPDATES.getKeybind()) {

            }
            return true;
        }
        return false;
    }*/
}
