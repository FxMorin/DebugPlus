package ca.fxco.debugplus;

import ca.fxco.debugplus.config.Configs;
import ca.fxco.debugplus.event.InputHandler;
import ca.fxco.debugplus.event.RenderHandler;
import ca.fxco.debugplus.hotkeys.KeyCallbacks;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;

public class InitManager implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

        RenderHandler renderer = RenderHandler.getInstance();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        //RenderEventHandler.getInstance().registerTooltipLastRenderer(renderer);
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

        //TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());

        KeyCallbacks.init();
    }
}