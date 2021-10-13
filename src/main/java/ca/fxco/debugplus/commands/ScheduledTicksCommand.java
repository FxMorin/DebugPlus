package ca.fxco.debugplus.commands;

import ca.fxco.debugplus.DebugPlus;
import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererBox;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.ScheduledTick;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static ca.fxco.debugplus.ClientCommands.sendToPlayer;

public class ScheduledTicksCommand extends baseCommand {

    public ScheduledTicksCommand(String name) {super(name);}

    public void register(LiteralArgumentBuilder<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> theCommand = CommandManager.literal(getName())
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
                        .executes((c) -> executeScheduleTicks(false)));
        dispatcher.then(theCommand);
    }

    public static int executeScheduleTicks(boolean blocks) {
        ClientPlayerEntity player = DebugPlus.MC.player;
        if (RendererToggles.DEBUG_OVERLAY_BOX.getBooleanValue()) {
            OverlayRendererBox.clear(blocks ? OverlayRendererBox.RENDER_MODE.SCHEDULED_BLOCK_TICKS : OverlayRendererBox.RENDER_MODE.SCHEDULED_FLUID_TICKS);
            ServerWorld world = DebugPlus.MC.getServer().getWorld(player.world.getRegistryKey());
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
            sendToPlayer(StringUtils.translate("debugplus.commands.require.debugoverlaybox"));
        }
        return 1;
    }
}
