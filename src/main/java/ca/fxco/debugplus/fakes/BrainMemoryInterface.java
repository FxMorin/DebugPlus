package ca.fxco.debugplus.fakes;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface BrainMemoryInterface<E extends LivingEntity> {
    Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> getAllMobMemories();
    Map<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>> getAllMobTasks();
    Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> getAllMobSensors();
    Set<Activity> getAllMobPossibleActivities();
}
