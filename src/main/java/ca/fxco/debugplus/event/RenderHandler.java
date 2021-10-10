package ca.fxco.debugplus.event;

import ca.fxco.debugplus.renderer.OverlayRenderer;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RenderHandler implements IRenderer {
    private static final RenderHandler INSTANCE = new RenderHandler();

    private final MinecraftClient mc;
    private final Map<ChunkPos, CompletableFuture<WorldChunk>> chunkFutures = new HashMap<>();
    @Nullable
    private WorldChunk cachedClientChunk;


    public RenderHandler() {
        this.mc = MinecraftClient.getInstance();
    }

    public static RenderHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix) {
        if (this.mc.world != null && this.mc.player != null && !this.mc.options.hudHidden) {
            OverlayRenderer.renderOverlays(matrixStack, projMatrix, this.mc);
        }
    }

    @Nullable
    private BlockEntity getTargetedBlockEntity(World world, MinecraftClient mc) {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos posLooking = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            WorldChunk chunk = this.getChunk(new ChunkPos(posLooking));
            // The method in World now checks that the caller is from the same thread...
            return chunk != null ? chunk.getBlockEntity(posLooking) : null;
        }
        return null;
    }

    @Nullable
    private BlockState getTargetedBlock(MinecraftClient mc) {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos posLooking = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            return mc.world.getBlockState(posLooking);
        }
        return null;
    }

    @Nullable
    private WorldChunk getChunk(ChunkPos chunkPos) {
        CompletableFuture<WorldChunk> future = this.chunkFutures.get(chunkPos);
        if (future == null) {
            future = this.setupChunkFuture(chunkPos);
        }
        return future.getNow(null);
    }

    private CompletableFuture<WorldChunk> setupChunkFuture(ChunkPos chunkPos) {
        IntegratedServer server = this.mc.getServer();
        CompletableFuture<WorldChunk> future = null;
        if (server != null) {
            ServerWorld world = server.getWorld(this.mc.world.getRegistryKey());
            if (world != null) {
                future = world.getChunkManager().getChunkFutureSyncOnMainThread(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false)
                        .thenApply((either) -> either.map((chunk) -> (WorldChunk) chunk, (unloaded) -> null) );
            }
        }
        if (future == null) {
            future = CompletableFuture.completedFuture(this.getClientChunk(chunkPos));
        }
        this.chunkFutures.put(chunkPos, future);
        return future;
    }

    private WorldChunk getClientChunk(ChunkPos chunkPos) {
        if (this.cachedClientChunk == null || !this.cachedClientChunk.getPos().equals(chunkPos)) {
            this.cachedClientChunk = this.mc.world.getChunk(chunkPos.x, chunkPos.z);
        }
        return this.cachedClientChunk;
    }

    private void resetCachedChunks() {
        this.chunkFutures.clear();
        this.cachedClientChunk = null;
    }
}
