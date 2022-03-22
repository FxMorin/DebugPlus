package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.utils.DataStructures;
import ca.fxco.debugplus.utils.RendererUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class OverlayRendererMobTarget extends OverlayRendererBase {

    private static final Color4f PurpleColor = new Color4f(1f,0f,1f,1f);

    public static final List<DataStructures.Line> lines = new ArrayList<>();

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_MOB_TARGET.getBooleanValue();}

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {return true;}

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);
        if (lines.size() > 0) {
            List<DataStructures.Line> temp = new ArrayList<>(lines);
            temp.forEach((line) -> RendererUtils.drawLine(line.start.subtract(cameraPos), line.end.subtract(cameraPos), PurpleColor, BUFFER_2));
        }
        BUFFER_2.end();
        renderLines.uploadData(BUFFER_2);
    }
}
