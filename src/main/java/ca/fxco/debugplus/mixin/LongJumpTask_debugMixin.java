package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.renderer.OverlayRendererGoatJump;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LongJumpTask.class)
public class LongJumpTask_debugMixin<E extends MobEntity> extends Task<E> {

    public LongJumpTask_debugMixin(Map<MemoryModuleType<?>, MemoryModuleState> map) {super(map);}


    @Inject(
            method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/MobEntity;)Z",
            at = @At("RETURN")
    )
    protected void beforeRun(ServerWorld serverWorld, MobEntity mobEntity, CallbackInfoReturnable<Boolean> cir) {
        OverlayRendererGoatJump.clear();
    }


    @Redirect(
            method = "canReach(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityDimensions;getBoxAt(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Box;"
            ))
    private Box grabVelocity(EntityDimensions entityDimensions, Vec3d pos) {
        Box box = entityDimensions.getBoxAt(pos);
        if (RendererToggles.DEBUG_GOAT_JUMPING.getBooleanValue()) {
            OverlayRendererGoatJump.addBox(box);
        }
        return box;
    }
}
