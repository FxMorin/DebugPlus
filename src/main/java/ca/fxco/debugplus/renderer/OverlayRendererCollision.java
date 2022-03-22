package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.DataStructures;
import ca.fxco.debugplus.utils.MiscUtils;
import ca.fxco.debugplus.utils.RendererUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class OverlayRendererCollision extends OverlayRendererBase {

    private static final Color4f RedColor = new Color4f(1f,0f,0f,1f);

    private static Color4f LineColor = new Color4f(1f,0f,1f,1f);

    private static final List<Box> BoxList = new ArrayList<>();

    private static final List<DataStructures.Line> LineList = new ArrayList<>();

    public static void addBox(Box box) {BoxList.add(box);}

    public static void addLine(Vec3d vec1,Vec3d vec2) {LineList.add(new DataStructures.Line(vec1,vec2));}

    public static void isColliding(boolean result) {
        LineColor = result ? new Color4f(0f,1f,0f,1f) : new Color4f(1f,0f,1f,1f);
    }

    public static void clear() {
        LineList.clear();
        BoxList.clear();
    }

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_ENTITY_COLLISION.getBooleanValue();}

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
                fi.dy.masa.malilib.render.RenderUtils.drawBoxAllEdgesBatchedLines(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, RedColor, BUFFER_2);
            }
        }
        if (LineList.size() > 0) {
            List<DataStructures.Line> temp = new ArrayList<>(LineList);
            temp.forEach((line) -> {
                if (line != null) {
                    RendererUtils.drawLine(line.start.subtract(cameraPos), line.end.subtract(cameraPos), LineColor, BUFFER_2);
                }
            });
        }
        boxList.clear();
        BUFFER_2.end();
        renderLines.uploadData(BUFFER_2);
    }
}
