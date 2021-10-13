package ca.fxco.debugplus;

import ca.fxco.debugplus.commands.HelpCommand;
import ca.fxco.debugplus.commands.ScheduledTicksCommand;
import ca.fxco.debugplus.commands.baseCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientCommands {

    public static Map<String,baseCommand> commandList = new HashMap<>();

    public static final String PREFIX = "debugplus";
    public static final String TXT_PREFIX = StringUtils.translate("debugplus.commands.prefix");

    public static void initializeCommands() {
        commandList.put("scheduled-ticks",new ScheduledTicksCommand("scheduled-ticks"));
        commandList.put("help",new HelpCommand("help"));
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        initializeCommands();
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(PREFIX);
        for (baseCommand cmd : commandList.values()) {
            cmd.register(builder);
        }
        dispatcher.register(builder);
    }

    public static void sendToPlayer(String str) {
        if (DebugPlus.MC.player != null) {
            DebugPlus.MC.player.sendMessage(new LiteralText(TXT_PREFIX+str), false);
        }
    }

    public static boolean isClientSideCommand(String[] args) {
        return (args.length > 0 && PREFIX.equals(args[0]));
    }

    public static int executeCommand(StringReader reader) {
        ClientPlayerEntity player = DebugPlus.MC.player;
        try {
            return player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
        } catch (Exception e) {
            DebugPlus.logger.warn("Unable to execute command: "+e);
            return 1;
        }
    }

    public static class FakeCommandSource extends ServerCommandSource {
        public FakeCommandSource(ClientPlayerEntity player) {
            super(player, player.getPos(), player.getRotationClient(), null, 0, player.getEntityName(), player.getName(), null, player);
        }

        public Collection<String> getPlayerNames() {
            return DebugPlus.MC.getNetworkHandler().getPlayerList().stream().map(e -> e.getProfile().getName()).collect(Collectors.toList());
        }
    }
}