package ca.fxco.debugplus.commands;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererBox;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.ScheduledTick;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ClientCommands {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final String PREFIX = "debugplus";
    public static final String TXT_PREFIX = "§8[§2DebugPlus§8] §f";

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(PREFIX);
        builder.then(
                CommandManager.literal("scheduled-ticks")
                        .then(CommandManager.literal("blocks")
                                .then(CommandManager.literal("clear").executes((c) -> {
                                    OverlayRendererBox.clear(OverlayRendererBox.RENDER_MODE.SCHEDULED_BLOCK_TICKS);
                                    return 1;
                                }))
                                .executes((c) -> executeScheduleTicks(true)))
                        .then(CommandManager.literal("fluids")
                                .then(CommandManager.literal("clear").executes((c) -> {
                                    OverlayRendererBox.clear(OverlayRendererBox.RENDER_MODE.SCHEDULED_FLUID_TICKS);
                                    return 1;
                                }))
                                .executes((c) -> executeScheduleTicks(false)))
                .then(CommandManager.literal("help").executes((context) -> helpCommand())));
        dispatcher.register(builder);
    }

    public static int helpCommand() {
        sendToPlayer("§2Help Menu§f\n" +
                " help - TODO, add other commands to help menu!");
        return 1;
    }

    public static int executeScheduleTicks(boolean blocks) {
        ClientPlayerEntity player = mc.player;
        if (RendererToggles.DEBUG_OVERLAY_BOX.getBooleanValue()) {
            OverlayRendererBox.clear(blocks ? OverlayRendererBox.RENDER_MODE.SCHEDULED_BLOCK_TICKS : OverlayRendererBox.RENDER_MODE.SCHEDULED_FLUID_TICKS);
            ServerWorld world = mc.getServer().getWorld(player.world.getRegistryKey());
            AtomicInteger count = new AtomicInteger();
            AtomicLong lastTime = new AtomicLong(0L);
            AtomicLong currentColor = new AtomicLong(0L);
            AtomicBoolean noLongerInTick = new AtomicBoolean(false);
            if (blocks) {
                List<ScheduledTick<Block>> scheduledTicksList = world.getBlockTickScheduler().getScheduledTicks(new BlockBox(player.getBlockX() - 32, world.getBottomY(), player.getBlockZ() - 32, player.getBlockX() + 32, world.getTopY(), player.getBlockZ() + 32), false, false);
                if (scheduledTicksList.size() > 0) {
                    long timeDiff = (scheduledTicksList.get(scheduledTicksList.size() - 1).time - world.getTime()) + 2;
                    lastTime.set(world.getTime() + 1);
                    scheduledTicksList.forEach(scheduledTick -> {
                        if (scheduledTick != null) {
                            if (scheduledTick.time > lastTime.get()) {
                                noLongerInTick.set(true);
                                lastTime.set(scheduledTick.time);
                                currentColor.getAndIncrement();
                            }
                            OverlayRendererBox.addBox(OverlayRendererBox.RENDER_MODE.SCHEDULED_BLOCK_TICKS,world.getTime(), new BlockBox(scheduledTick.pos), Color4f.fromColor(Color.HSBtoRGB((360.0f / (timeDiff * 1.0f)) * currentColor.intValue(), 0.9f, 0.9f), 0.9f), noLongerInTick.get() ? "> " + (scheduledTick.time - world.getTime()) : String.valueOf(count.getAndIncrement()), 200L, true);
                        }
                    });
                }
            } else {
                List<ScheduledTick<Fluid>> scheduledTicksList = world.getFluidTickScheduler().getScheduledTicks(new BlockBox(player.getBlockX() - 32, world.getBottomY(), player.getBlockZ() - 32, player.getBlockX() + 32, world.getTopY(), player.getBlockZ() + 32), false, false);
                if (scheduledTicksList.size() > 0) {
                    long timeDiff = (scheduledTicksList.get(scheduledTicksList.size() - 1).time - world.getTime()) + 2;
                    lastTime.set(world.getTime() + 1);
                    scheduledTicksList.forEach(scheduledTick -> {
                        if (scheduledTick != null) {
                            if (scheduledTick.time > lastTime.get()) {
                                noLongerInTick.set(true);
                                lastTime.set(scheduledTick.time);
                                currentColor.getAndIncrement();
                            }
                            OverlayRendererBox.addBox(OverlayRendererBox.RENDER_MODE.SCHEDULED_FLUID_TICKS,world.getTime(), new BlockBox(scheduledTick.pos), Color4f.fromColor(Color.HSBtoRGB((360.0f / (timeDiff * 1.0f)) * currentColor.intValue(), 0.9f, 0.9f), 0.9f), noLongerInTick.get() ? "> " + (scheduledTick.time - world.getTime()) : String.valueOf(count.getAndIncrement()), 200L, true);
                        }
                    });
                }
            }
        } else {
            sendToPlayer("Debug Overlay Box should be enabled to use this command!");
        }
        return 1;
    }

    public static void sendToPlayer(String str) {
        if (mc.player != null) {
            mc.player.sendMessage(new LiteralText(TXT_PREFIX+str), false);
        }
    }

    public static boolean isClientSideCommand(String[] args) {
        return (args.length > 0 && PREFIX.equals(args[0]));
    }

    public static int executeCommand(StringReader reader) {
        ClientPlayerEntity player = mc.player;
        try {
            return player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
        } catch (Exception e) {
            //TODO ADD Error message here (LOGGER)
            return 1;
        }
    }

    public static class FakeCommandSource extends ServerCommandSource {
        public FakeCommandSource(ClientPlayerEntity player) {
            super(player, player.getPos(), player.getRotationClient(), null, 0, player.getEntityName(), player.getName(), null, player);
        }

        public Collection<String> getPlayerNames() {
            return mc.getNetworkHandler().getPlayerList().stream().map(e -> e.getProfile().getName()).collect(Collectors.toList());
        }
    }
}