package ca.fxco.debugplus.event;

import ca.fxco.debugplus.ModInfo;
import ca.fxco.debugplus.config.Configs;
import ca.fxco.debugplus.config.RendererToggles;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;

public class InputHandler implements IKeybindProvider, IMouseInputHandler {
    private static final InputHandler INSTANCE = new InputHandler();

    private InputHandler() {super();}

    public static InputHandler getInstance() {return INSTANCE;}

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (RendererToggles toggle : RendererToggles.values()) {
            manager.addKeybindToMap(toggle.getKeybind());
        }
        for (IHotkey hotkey : Configs.Generic.HOTKEY_LIST) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(ModInfo.MOD_NAME, "debugplus.hotkeys.category.generic_hotkeys", Configs.Generic.HOTKEY_LIST);
        manager.addHotkeysForCategory(ModInfo.MOD_NAME, "debugplus.hotkeys.category.renderer_toggle_hotkeys", ImmutableList.copyOf(RendererToggles.values()));
    }

    /*@Override
    public boolean onMouseScroll(int mouseX, int mouseY, double dWheel) {
        // Not in a GUI
        if (GuiUtils.getCurrentScreen() == null && dWheel != 0) {
            if (RendererToggle.OVERLAY_SLIME_CHUNKS_OVERLAY.getBooleanValue() &&
                    RendererToggle.OVERLAY_SLIME_CHUNKS_OVERLAY.getKeybind().isKeybindHeld())
            {
                OverlayRendererSlimeChunks.overlayTopY += (dWheel < 0 ? 1 : -1);
                KeyCallbackAdjustable.setValueChanged();
                return true;
            }
        }

        return false;
    }*/
}