package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.MiscUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.IntBoundingBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

import java.util.*;

public class OverlayRendererBox extends OverlayRendererBase {

    public static enum RENDER_MODE {
        SCHEDULED_BLOCK_TICKS,
        SCHEDULED_FLUID_TICKS
    }

    public static class BoxData {
        public final long time;
        public final BlockBox box;
        public final Color4f color;
        public final String msg;
        public final long age;
        public final boolean outline;

        public BoxData(long time, BlockBox box, Color4f color, String msg, long age, boolean outline) {
            this.time = time;
            this.box = box;
            this.color = color;
            this.msg = msg;
            this.age = age;
            this.outline = outline;
        }
    }

    private static final Map<RENDER_MODE,List<BoxData>> BoxList = new HashMap<>();

    public static void addBox(RENDER_MODE type,BoxData box) {BoxList.get(type).add(box);}

    public static void addBox(RENDER_MODE type,long time,BlockBox box, Color4f color, String msg) {addBox(type,time,box,color,msg,200L);}

    public static void addBox(RENDER_MODE type,long time,BlockBox box, Color4f color, String msg, long age) {addBox(type,time,box,color,msg,age,false);}

    public static void addBox(RENDER_MODE type,long time,BlockBox box, Color4f color, String msg, long age, boolean outline) {BoxList.get(type).add(new BoxData(time,box,color,msg,age,outline));}

    public static void clear(RENDER_MODE type) {
        BoxList.get(type).clear();
    }

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_OVERLAY_BOX.getBooleanValue();}

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {
        return true;
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        BoxList.forEach((type,c) -> {
            List<BoxData> boxList = BoxList.get(type);
            int maxRange = (mc.options.viewDistance + 6) * 16;
            long clientTime = mc.world.getTime();
            RenderObjectBase renderQuads = this.renderObjects.get(0);
            RenderObjectBase renderLines = this.renderObjects.get(1);
            BUFFER_1.begin(renderQuads.getGlMode(), VertexFormats.POSITION_COLOR);
            BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
            for (int i = boxList.size() - 1; i >= 0; i--) {
                BoxData data = boxList.get(i);
                if (data == null) break;
                if (MiscUtils.isRenderWithinRange(data.box, this.lastUpdatePos, maxRange)) {
                    long m = clientTime - data.time;
                    if (m > data.age) {
                        BoxList.remove(i);
                    } else {
                        Color4f color = new Color4f(data.color.r, data.color.g, data.color.b, data.color.a - (data.color.a / data.age) * (float) m);
                        if (data.outline) {
                            IntBoundingBox bb = IntBoundingBox.fromVanillaBox(data.box);
                            fi.dy.masa.malilib.render.RenderUtils.drawBoxAllEdgesBatchedLines(bb.minX - cameraPos.x, bb.minY - cameraPos.y, bb.minZ - cameraPos.z, bb.maxX + 1 - cameraPos.x, bb.maxY + 1 - cameraPos.y, bb.maxZ + 1 - cameraPos.z, color, BUFFER_2);
                        } else {
                            fi.dy.masa.malilib.render.RenderUtils.drawBox(IntBoundingBox.fromVanillaBox(data.box), cameraPos, color, BUFFER_1, BUFFER_2);
                        }
                    }
                }
            }
            for (BoxData data : boxList) {
                if (data != null && !Objects.equals(data.msg, "")) {
                    BlockPos center = data.box.getCenter();
                    fi.dy.masa.malilib.render.RenderUtils.drawTextPlate(Collections.singletonList(data.msg), center.getX() + 0.5d, center.getY() + 0.5d, center.getZ() + 0.5d, 0.025f);
                }
            }
            BUFFER_1.end();
            BUFFER_2.end();
            renderQuads.uploadData(BUFFER_1);
            renderLines.uploadData(BUFFER_2);
        });
    }
}
