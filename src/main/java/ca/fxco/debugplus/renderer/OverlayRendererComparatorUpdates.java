package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.MiscUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.IntBoundingBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class OverlayRendererComparatorUpdates extends OverlayRendererBase {

    private static final Color4f getBlockStateColor = new Color4f(0.75f,0f,0f,0.12f);
    private static final Color4f giveBlockUpdateColor = new Color4f(0.9f,0f,0f,0.45f);

    public static class BoxData {
        public final long time;
        public final BlockBox box;
        public final boolean update;
        public final long age;

        public BoxData(long time,BlockBox box, boolean update, long age) {
            this.time = time;
            this.box = box;
            this.update = update;
            this.age = age;
        }
    }

    private static final List<BoxData> BoxList = new ArrayList<>();

    public static void addBox(BoxData box) {BoxList.add(box);}

    public static void addBox(long time,BlockBox box, boolean update) {addBox(time,box,update,50L);}

    public static void addBox(long time,BlockBox box, boolean update, long age) {BoxList.add(new BoxData(time,box,update,age));}

    public static void clear() {
        BoxList.clear();
    }

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_COMPARATOR_UPDATES.getBooleanValue();}

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {
        return true;
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        int maxRange = (mc.options.viewDistance + 6) * 16;
        long clientTime = mc.world.getTime();
        RenderObjectBase renderQuads = this.renderObjects.get(0);
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_1.begin(renderQuads.getGlMode(), VertexFormats.POSITION_COLOR);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
        for (int i = BoxList.size() - 1; i >= 0; i--) {
            BoxData data = BoxList.get(i);
            if (data == null) break;
            if (MiscUtils.isRenderWithinRange(data.box, this.lastUpdatePos, maxRange)) {
                long m = clientTime - data.time;
                if (m > data.age) {
                    BoxList.remove(i);
                } else {
                    Color4f choosen = data.update ? giveBlockUpdateColor : getBlockStateColor;
                    fi.dy.masa.malilib.render.RenderUtils.drawBox(IntBoundingBox.fromVanillaBox(data.box), cameraPos, new Color4f(choosen.r, choosen.g, choosen.b, choosen.a - (choosen.a / data.age) * (float) m), BUFFER_1, BUFFER_2);
                }
            }
        }
        BUFFER_1.end();
        BUFFER_2.end();
        renderQuads.uploadData(BUFFER_1);
        renderLines.uploadData(BUFFER_2);
    }
}
