package ca.fxco.debugplus.renderer;

import ca.fxco.debugplus.config.RendererToggles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayRendererMobTasks extends OverlayRendererBase {

    //private static final Color4f WhiteColor = new Color4f(1f,1f,1f,1f);

    private static final Map<Integer,List<String>> plates = new ConcurrentHashMap<>();

    @Override
    public boolean shouldRender(MinecraftClient mc) {return RendererToggles.DEBUG_MOB_MEMORIES.getBooleanValue();}

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc) {return true;}

    public static void addTextPlates(int mobId, List<String> memories) {
        if (plates.get(mobId) == null) {
            plates.put(mobId,memories);
        } else {
            plates.get(mobId).addAll(memories);
        }
    }

    public static void clear() {
        plates.clear();
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, MinecraftClient mc) {
        if (plates.size() > 0 && mc.world != null) {
            for (Map.Entry<Integer,List<String>> plate : new ConcurrentHashMap<>(plates).entrySet()) {
                Entity follow = mc.world.getEntityById(plate.getKey());
                if (follow != null) {
                    List<String> text = new ArrayList<>(plate.getValue());
                    fi.dy.masa.malilib.render.RenderUtils.drawTextPlate(text, follow.getX(), follow.getY() + follow.getHeight() + 1.2f + (text.size() * 0.1F), follow.getZ(), 0.025f);
                }
            }
        }
    }
}
