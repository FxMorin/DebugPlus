package ca.fxco.debugplus.utils;

import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Vec3d;

public class RendererUtils {

    public static void drawLine(double X1, double Y1, double Z1, double X2, double Y2, double Z2, Color4f color, BufferBuilder buffer) {
        buffer.vertex(X1, Y1, Z1).color(color.r, color.g, color.b, color.a).next();
        buffer.vertex(X2, Y2, Z2).color(color.r, color.g, color.b, color.a).next();
    }

    public static void drawLine(Vec3d vec1, Vec3d vec2, Color4f color, BufferBuilder buffer) {
        buffer.vertex(vec1.getX(), vec1.getY(), vec1.getZ()).color(color.r, color.g, color.b, color.a).next();
        buffer.vertex(vec2.getX(), vec2.getY(), vec2.getZ()).color(color.r, color.g, color.b, color.a).next();
    }
}
