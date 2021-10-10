package ca.fxco.debugplus;

import ca.fxco.debugplus.config.Configs;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DebugPlus implements ModInitializer {
    public static final Logger logger = LogManager.getLogger(ModInfo.MOD_ID);
    public static boolean isRunningCarpet = false;
    public static boolean isRunningQuickCarpet = false;
    public static Field isCarpetTickFrozen;
    public static Method isQuickCarpetTickFrozen;
    public static Class CarpetClass;

    @Override
    public void onInitialize() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitManager());
        isRunningCarpet = FabricLoader.getInstance().isModLoaded("carpet");
        isRunningQuickCarpet = FabricLoader.getInstance().isModLoaded("quickcarpet");
        if (isRunningCarpet) {
            try {
                CarpetClass = Class.forName("carpet.helpers.TickSpeed");
                isCarpetTickFrozen = CarpetClass.getDeclaredField("process_entities");
            } catch (ClassNotFoundException | NoSuchFieldException e) {e.printStackTrace();}
        } else if (isRunningQuickCarpet) {
            try {
                CarpetClass = Class.forName("quickcarpet.helper.TickSpeed");
                isQuickCarpetTickFrozen = CarpetClass.getMethod("isPaused");
            } catch (ClassNotFoundException | NoSuchMethodException e) {e.printStackTrace();}
        }
    }

    public static boolean isTickFrozen() {
        if (isRunningCarpet) {
            try {
                return !((boolean) isCarpetTickFrozen.get(CarpetClass));
            } catch (IllegalAccessException ignored) {}
        } else if (isRunningQuickCarpet) {
            try {
                return (boolean) isQuickCarpetTickFrozen.invoke(CarpetClass);
            } catch (InvocationTargetException | IllegalAccessException ignored) {}
        }
        return false;
    }

    public static void printDebug(String key, Object... args) {
        if (Configs.Generic.DEBUG_MESSAGES.getBooleanValue()) {
            logger.info(key, args);
        }
    }
}
