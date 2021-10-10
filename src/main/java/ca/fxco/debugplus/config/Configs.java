package ca.fxco.debugplus.config;

import ca.fxco.debugplus.ModInfo;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.List;

public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = ModInfo.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static class Generic {

        public static final ConfigHotkey  OPEN_CONFIG_GUI                 = new ConfigHotkey("openConfigGui", "R,C", "A hotkey to open the in-game Config GUI");
        public static final ConfigBoolean DEBUG_MESSAGES                  = new ConfigBoolean("debugMessages", false, "Enables some debug messages in the game console");
        public static final ConfigBoolean RAINBOW_MODE                    = new ConfigBoolean("rainbowMode", false, "All colors are rainbow");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                DEBUG_MESSAGES,
                RAINBOW_MODE
        );

        public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
                OPEN_CONFIG_GUI
        );
    }

    @Override
    public void load() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "RendererHotkeys", "RendererToggles", ImmutableList.copyOf(RendererToggles.values()));

                //int version = JsonUtils.getIntegerOrDefault(root, "config_version", 0);
            }
        }
    }

    @Override
    public void save() {
        File dir = FileUtils.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "RendererHotkeys", "RendererToggles", ImmutableList.copyOf(RendererToggles.values()));

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
}
