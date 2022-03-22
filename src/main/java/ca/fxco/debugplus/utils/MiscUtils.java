package ca.fxco.debugplus.utils;

import fi.dy.masa.malilib.util.IntBoundingBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public static String getReadableValue(Object v) {
        return getReadableValue(v,0);
    }

    public static String getReadableValue(Object v, int level) {
        if (v instanceof GlobalPos pos) {
            return pos.getPos().toShortString();
        } else if (v instanceof Entity) {
            return "entity: "+((Entity)v).getDisplayName().getString();
        } else if (v instanceof BlockPos) {
            return ((BlockPos) v).toShortString();
        } else if (v instanceof Number) {
            return v.toString();
        } else if (v instanceof Boolean) {
            return v.toString();
        } else if (v instanceof UUID) {
            return v.toString();
        } else if (v instanceof DamageSource source) {
            return "source: "+source.getName()+(source.getAttacker()==null?"":", attacker: "+source.getAttacker().getDisplayName().getString());
        } else if (v instanceof LookTarget) {
            return ((LookTarget)v).getBlockPos().toShortString();
        } else if (v instanceof WalkTarget) {
            return ((WalkTarget)v).getLookTarget().getBlockPos().toShortString()+", speed: "+((WalkTarget) v).getSpeed();
        } else if (v instanceof Set s) {
            if (s.isEmpty()) return "[EmptySet]";
            if (level < 3 && s.size() < 3) {
                StringBuilder concat = new StringBuilder("[");
                for (Object o : s) concat.append(getReadableValue(o, level + 1)).append(",");
                return concat.substring(0,concat.length()-1)+"]";
            }
            return "[Set ("+s.size()+")]";
        } else if (v instanceof List l) {
            if (l.isEmpty()) return "[EmptyList]";
            Object el = l.get(0);
            if (el instanceof Entity) {
                if (l.size() > 3) {
                    String main = Arrays.toString(l.subList(0,3).stream().map(o -> ((Entity)o).getDisplayName().getString()).toArray());
                    return main+", etc... ("+l.size()+")";
                }
                return Arrays.toString(l.stream().map(o -> ((Entity)o).getDisplayName().getString()).toArray());
            } else if (el instanceof GlobalPos) {
                if (l.size() > 3) {
                    String main = Arrays.toString(l.subList(0,3).stream().map(o -> ((Entity)o).getDisplayName().getString()).toArray());
                    return main+", etc... ("+l.size()+")";
                }
                return Arrays.toString(l.stream().map(o -> (((GlobalPos)o).getPos().toShortString())).toArray());
            }
        }
        return v.getClass().getSimpleName();
    }
}
