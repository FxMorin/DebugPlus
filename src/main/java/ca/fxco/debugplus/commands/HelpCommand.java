package ca.fxco.debugplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static ca.fxco.debugplus.ClientCommands.commandList;
import static ca.fxco.debugplus.ClientCommands.sendToPlayer;

public class HelpCommand extends baseCommand {

    public HelpCommand(String name) {super(name);}

    public void register(LiteralArgumentBuilder<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> theCommand = CommandManager.literal(getName())
            .executes((context) -> helpCommand());
        dispatcher.then(theCommand);
    }

    public static int helpCommand() {
        StringBuilder response = new StringBuilder();
        for (baseCommand cmd : commandList.values()) {
            System.out.println(cmd.getName()+" - "+cmd.getDescription());
            response.append(cmd.getName()).append(" - ").append(cmd.getDescription()).append("\n");
        }
        sendToPlayer(StringUtils.translate("debugplus.commands.helpmenu") + response);
        return 1;
    }
}
