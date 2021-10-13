package ca.fxco.debugplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static ca.fxco.debugplus.ClientCommands.sendToPlayer;

public class baseCommand {

    private String commandName;
    private String commandDescription;

    public baseCommand(String name) {
        this.commandName = name;
        this.commandDescription = StringUtils.translate("debugplus.command."+name+".desc");
    }

    public void register(LiteralArgumentBuilder<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> theCommand = CommandManager.literal(this.commandName)
                .executes((c) -> {
                    sendToPlayer(StringUtils.translate("debugplus.commands.notimplemented"));
                    return 1;
                });
        dispatcher.then(theCommand);
    }

    public String getDescription() {
        return this.commandDescription;
    }

    public void setDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    public String getName() {
        return this.commandName;
    }

    public void setName(String newCommandName) {
        this.commandName = newCommandName;
    }
}
