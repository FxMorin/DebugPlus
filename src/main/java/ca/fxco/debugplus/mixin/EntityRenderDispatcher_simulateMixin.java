package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.config.RendererToggles;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcher_simulateMixin {


    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    shift = At.Shift.BEFORE
            )
    )
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (RendererToggles.DEBUG_ENTITY_COLLISION.getBooleanValue() && !entity.noClip) {
            renderCollisionHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, entity.getVelocity(),tickDelta);
        }
    }

    private static void renderCollisionHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, Vec3d offset, float tickDelta) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ()).offset(offset);
        WorldRenderer.drawBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 0.9F);
    }
}
