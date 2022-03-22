package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererComparatorUpdates;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
        require = {
                @Condition(value = "minecraft", versionPredicates = "<1.19-alpha.22.11.a"),
        }
)
@Mixin(World.class)
public class World_comparatorUpdatesMixin {


    @Redirect(
            method="updateComparators",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            )
    )
    public BlockState getBlockState(World world, BlockPos pos) {
        if (RendererToggles.DEBUG_COMPARATOR_UPDATES.getBooleanValue() && !world.isClient) {
            OverlayRendererComparatorUpdates.addBox(world.getTime(), new BlockBox(pos), false);
        }
        return world.getBlockState(pos);
    }


    @Redirect(
            method="updateComparators",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"
            )
    )
    public void neighborUpdate(BlockState instance, World world, BlockPos pos, Block block, BlockPos posFrom, boolean b) {
        if (RendererToggles.DEBUG_COMPARATOR_UPDATES.getBooleanValue() && !world.isClient) {
            OverlayRendererComparatorUpdates.addBox(world.getTime(), new BlockBox(pos), true);
        }
        instance.neighborUpdate(world,pos,block,posFrom,b);
    }
}
