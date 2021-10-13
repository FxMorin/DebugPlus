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
import java.util.ListIterator;

public class OverlayRendererGoatJump extends OverlayRendererBase {

    private static final Color4f WhiteColor = new Color4f(1f,1f,1f,0.5f);

    private static final List<Box> BoxList = new ArrayList<>();

    public static void addBox(Box box) {BoxList.add(box);}

    public static void clear() {
        BoxList.clear();
    }

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_GOAT_JUMPING.getBooleanValue();}

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {
        return BoxList.size() > 0;
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
        List<Box> boxList = new ArrayList<>(BoxList);
        for (ListIterator<Box> it = boxList.listIterator(); it.hasNext(); ) {
            Box box = it.next();
            if (MiscUtils.isRenderWithinRange(box, this.lastUpdatePos, (mc.options.viewDistance + 6) * 16)) {
                Box bb = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                fi.dy.masa.malilib.render.RenderUtils.drawBoxAllEdgesBatchedLines(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, WhiteColor, BUFFER_2);
            }
        }
        boxList.clear();
        BUFFER_2.end();
        renderLines.uploadData(BUFFER_2);
    }
}
