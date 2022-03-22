package ca.fxco.debugplus.utils;

import net.minecraft.util.math.Vec3d;

public class DataStructures {

    public static class Line {
        public final Vec3d start;
        public final Vec3d end;

        public Line(Vec3d start, Vec3d end) {
            this.start = start;
            this.end = end;
        }
    }
}
