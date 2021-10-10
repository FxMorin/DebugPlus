package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererShapeUpdates;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockState_shapeUpdatesMixin {

    @Inject(method= "getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",at=@At("HEAD"))
    public void getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (RendererToggles.DEBUG_SHAPE_UPDATES.getBooleanValue()) {
            World temp = (World)world;
            if (!temp.isClient) {
                OverlayRendererShapeUpdates.INSTANCE.addShapeUpdate(temp.getTime(), pos);
            }
        }
    }
}
