package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.DebugPlus;
import ca.fxco.debugplus.config.RendererToggles;
import ca.fxco.debugplus.fakes.BrainMemoryInterface;
import ca.fxco.debugplus.renderer.OverlayRendererMobTarget;
import ca.fxco.debugplus.renderer.OverlayRendererMobTasks;
import ca.fxco.debugplus.utils.DataStructures;
import ca.fxco.debugplus.utils.MiscUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

//import static ca.fxco.debugplus.config.Configs.Generic.MOB_SEARCH_RADIUS;

@Mixin(ServerWorld.class)
public abstract class ServerWorld_tickMixin extends World {


    protected ServerWorld_tickMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Inject(
            method = "tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At("HEAD")
    )
    public void onServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (DebugPlus.MC != null && DebugPlus.MC.player != null && (RendererToggles.DEBUG_MOB_TARGET.getBooleanValue() || RendererToggles.DEBUG_MOB_MEMORIES.getBooleanValue())) {
            int radius = 32;//MOB_SEARCH_RADIUS.getIntegerValue();
            List<MobEntity> entities = this.getEntitiesByClass(MobEntity.class, new Box(DebugPlus.MC.player.getX() - radius, DebugPlus.MC.player.getY() - radius, DebugPlus.MC.player.getZ() - radius, DebugPlus.MC.player.getX() + radius, DebugPlus.MC.player.getY() + radius, DebugPlus.MC.player.getZ() + radius), (e) -> true);
            if (entities.size() > 0) {
                if (RendererToggles.DEBUG_MOB_TARGET.getBooleanValue()) {
                    OverlayRendererMobTarget.lines.clear();
                    for (MobEntity mob : entities) {
                        LivingEntity le = mob.getTarget();
                        if (le != null) {
                            OverlayRendererMobTarget.lines.add(new DataStructures.Line(mob.getEyePos(), le.getEyePos()));
                        }
                    }
                }
                OverlayRendererMobTasks.clear();
                if (RendererToggles.DEBUG_MOB_MEMORIES.getBooleanValue()) {
                    for (MobEntity mob : entities) {
                        Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = ((BrainMemoryInterface) mob.getBrain()).getAllMobMemories();
                        Iterator<Map.Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>>> memoryIterator = memories.entrySet().iterator();
                        List<String> memoryText = new ArrayList<>();
                        memoryText.add("Memories: ("+memories.size()+")");
                        while (memoryIterator.hasNext()) {
                            Map.Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry = memoryIterator.next();
                            if ((entry.getValue()).isPresent()) {
                                String memoryName = entry.getKey().toString().substring(10);
                                if (!Objects.equals(memoryName, "walk_target")) {
                                    String value = MiscUtils.getReadableValue(((Memory) ((Optional) entry.getValue()).get()).getValue());
                                    if (!Objects.equals(value, "") && !Objects.equals(value, "Path")) {
                                        memoryText.add(memoryName + ": " + value);
                                    }
                                }
                            }
                        }
                        OverlayRendererMobTasks.addTextPlates(mob.getId(), memoryText);
                    }
                }
                if (RendererToggles.DEBUG_MOB_SENSORS.getBooleanValue()) {
                    for (MobEntity mob : entities) {
                        Map<SensorType<? extends Sensor<?>>, Sensor<?>> sensors = ((BrainMemoryInterface) mob.getBrain()).getAllMobSensors();
                        Iterator<Map.Entry<SensorType<? extends Sensor<?>>, Sensor<?>>> sensorIterator = sensors.entrySet().iterator();
                        List<String> sensorText = new ArrayList<>();
                        sensorText.add("Sensors: ("+sensors.size()+")");
                        while (sensorIterator.hasNext()) {
                            Map.Entry<SensorType<? extends Sensor<?>>, Sensor<?>> entry = sensorIterator.next();
                            sensorText.add(Registry.SENSOR_TYPE.getId(entry.getKey()).getPath()+": ["+MiscUtils.getReadableValue(entry.getValue().getOutputMemoryModules())+"]");
                        }
                        OverlayRendererMobTasks.addTextPlates(mob.getId(), sensorText);
                    }
                }
                if (RendererToggles.DEBUG_MOB_TASKS.getBooleanValue()) {
                    for (MobEntity mob : entities) {
                        Map<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>> tasks = ((BrainMemoryInterface) mob.getBrain()).getAllMobTasks();
                        Iterator<Map.Entry<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>>> taskIterator = tasks.entrySet().iterator();
                        List<String> taskText = new ArrayList<>();
                        taskText.add("Tasks: ("+tasks.size()+")");
                        while (taskIterator.hasNext()) {
                            Map.Entry<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>> entry = taskIterator.next();
                            for (Map.Entry<Activity, Set<Task<? extends LivingEntity>>> activityTasks : entry.getValue().entrySet()) {
                                taskText.add(activityTasks.getKey().getId()+": ["+ MiscUtils.getReadableValue(activityTasks.getValue()) +"]");
                            }
                        }
                        OverlayRendererMobTasks.addTextPlates(mob.getId(), taskText);
                    }
                }
                if (RendererToggles.DEBUG_MOB_ACTIVITIES.getBooleanValue()) {
                    for (MobEntity mob : entities) {
                        Set<Activity> activities = ((BrainMemoryInterface) mob.getBrain()).getAllMobPossibleActivities();
                        List<String> activityText = new ArrayList<>();
                        activityText.add("Possible Activities: ("+activities.size()+")");
                        StringBuilder list = new StringBuilder("[");
                        for(Activity activity : activities) list.append(activity.getId()).append(",");;
                        activityText.add(list.substring(0,list.length() - 1) + "]");
                        OverlayRendererMobTasks.addTextPlates(mob.getId(), activityText);
                    }
                }
            }
        }
    }
}
