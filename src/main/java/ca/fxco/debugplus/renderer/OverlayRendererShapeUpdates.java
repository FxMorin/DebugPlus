package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.MiscUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class OverlayRendererShapeUpdates extends OverlayRendererBase {

    public static final OverlayRendererShapeUpdates INSTANCE = new OverlayRendererShapeUpdates();

    private static final Map<Long, Map<BlockPos, Integer>> shapeUpdates = Maps.newTreeMap(Ordering.natural().reverse());
    private final Color4f color = new Color4f(1f,0f,1f,1f); //Purple

    OverlayRendererShapeUpdates() {}

    @Override
    public boolean shouldRender(MinecraftClient mc) {
        return RendererToggles.DEBUG_SHAPE_UPDATES.getBooleanValue();
    }

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {
        return true;
    }

    public void addShapeUpdate(long time, BlockPos pos) {
        pos = pos.toImmutable();
        Map<BlockPos, Integer> map = OverlayRendererShapeUpdates.shapeUpdates.computeIfAbsent(time, l -> Maps.newHashMap());
        int i = map.getOrDefault(pos, 0);
        map.put(pos, i + 1);
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        int maxRange = (mc.options.viewDistance + 6) * 14; //16
        long clientTime = mc.world.getTime();
        double d = 0.0025D;
        Set<BlockPos> set = Sets.newHashSet();
        Map<BlockPos, Integer> map = Maps.newHashMap();
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator = OverlayRendererShapeUpdates.shapeUpdates.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<BlockPos, Integer>> nextEntry = iterator.next();
            Long time = nextEntry.getKey();
            Map<BlockPos, Integer> internalData = nextEntry.getValue();
            long age = clientTime - time;
            if (age > 200L) {
                iterator.remove();
            } else {
                for (Map.Entry<BlockPos, Integer> internalMap : internalData.entrySet()) {
                    BlockPos blockPos = internalMap.getKey();
                    if (MiscUtils.isRenderWithinRange(blockPos, this.lastUpdatePos, maxRange)) {
                        Integer integer = internalMap.getValue();
                        if (set.add(blockPos)) {
                            Box bb = new Box(blockPos).expand(0.002D).contract(d * (double) age).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                            fi.dy.masa.malilib.render.RenderUtils.drawBoxAllEdgesBatchedLines(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color, BUFFER_2);
                            map.put(blockPos, integer);
                        }
                    }
                }
            }
        }

        for (Map.Entry<BlockPos, Integer> internalMap : map.entrySet()) {
            BlockPos blockPos = internalMap.getKey();
            Integer integer = internalMap.getValue();
            fi.dy.masa.malilib.render.RenderUtils.drawTextPlate(Collections.singletonList(String.valueOf(integer)), blockPos.getX()+0.5f, blockPos.getY()+0.5f, blockPos.getZ()+0.5f, 0.025f);
        }
        BUFFER_2.end();
        renderLines.uploadData(BUFFER_2);
    }
}
