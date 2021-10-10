package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererRandomTicks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ServerWorld.class)
public class ServerWorld_randomTicksMixin {

    @Redirect(
            method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;randomTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
            ))
    public void onRandomBlockTick(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
        if (RendererToggles.DEBUG_RANDOM_TICKS.getBooleanValue()) {
            OverlayRendererRandomTicks.addBox(world.getTime(), new BlockBox(pos), true);
        }
        blockState.randomTick(world,pos,random);
    }


    @Redirect(
            method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;onRandomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
            ))
    public void onRandomFluidTick(FluidState fluidState, World world, BlockPos pos, Random random) {
        if (RendererToggles.DEBUG_RANDOM_TICKS.getBooleanValue()) {
            OverlayRendererRandomTicks.addBox(world.getTime(), new BlockBox(pos), false);
        }
        fluidState.onRandomTick(world,pos,random);
    }
}
