package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.MiscUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class OverlayRendererGoatJump extends OverlayRendererBase {

    private static final Color4f WhiteColor = new Color4f(1f,1f,1f,1f);

    private static final List<Box> BoxList = new ArrayList<>();

    public static void addBox(Box box) {BoxList.add(box);}

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
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
        for (int i = BoxList.size() - 1; i >= 0; i--) {
            Box box = BoxList.get(i);
            if (MiscUtils.isRenderWithinRange(box, this.lastUpdatePos, (mc.options.viewDistance + 6) * 16)) {
                box = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                fi.dy.masa.malilib.render.RenderUtils.drawBoxAllEdgesBatchedLines(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, WhiteColor, BUFFER_2);
            }
        }
        BUFFER_2.end();
        renderLines.uploadData(BUFFER_2);
    }
}
