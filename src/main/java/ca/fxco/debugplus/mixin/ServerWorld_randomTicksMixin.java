package ca.fxco.debugplus.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value=ServerWorld.class,priority=1100)
public class ServerWorld_randomTicksMixin {

    /*@Redirect(
            method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V",
            require = 0,
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
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;onRandomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
            ))
    public void onRandomFluidTick(FluidState fluidState, World world, BlockPos pos, Random random) {
        if (RendererToggles.DEBUG_RANDOM_TICKS.getBooleanValue()) {
            OverlayRendererRandomTicks.addBox(world.getTime(), new BlockBox(pos), false);
        }
        fluidState.onRandomTick(world,pos,random);
    }*/
}
