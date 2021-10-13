package ca.fxco.debugplus.utils;

import fi.dy.masa.malilib.util.IntBoundingBox;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {

    public static boolean isRenderWithinRange(BlockPos blockPos, BlockPos playerPos, int maxRange) {
        return playerPos.getX() >= (blockPos.getX() - maxRange) && playerPos.getX() <= (blockPos.getX() + maxRange) && playerPos.getZ() >= (blockPos.getZ() - maxRange) && playerPos.getZ() <= (blockPos.getZ() + maxRange);
    }
    public static boolean isRenderWithinRange(@Nullable BlockBox bb, BlockPos playerPos, int maxRange) {
        return bb != null && playerPos.getX() >= (bb.getMinX() - maxRange) && playerPos.getX() <= (bb.getMaxX() + maxRange) && playerPos.getZ() >= (bb.getMinZ() - maxRange) && playerPos.getZ() <= (bb.getMaxZ() + maxRange);
    }

    public static boolean isRenderWithinRange(@Nullable IntBoundingBox bb, BlockPos playerPos, int maxRange) {
        return bb != null && playerPos.getX() >= (bb.minX - maxRange) && playerPos.getX() <= (bb.maxX + maxRange) && playerPos.getZ() >= (bb.minZ - maxRange) && playerPos.getZ() <= (bb.maxZ + maxRange);
    }

    public static boolean isRenderWithinRange(@Nullable Box bb, BlockPos playerPos, int maxRange) {
        return bb != null && playerPos.getX() >= (bb.minX - maxRange) && playerPos.getX() <= (bb.maxX + maxRange) && playerPos.getZ() >= (bb.minZ - maxRange) && playerPos.getZ() <= (bb.maxZ + maxRange);
    }
}
