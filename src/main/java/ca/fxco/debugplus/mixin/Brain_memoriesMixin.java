package ca.fxco.debugplus.mixin;

import ca.fxco.debugplus.fakes.BrainMemoryInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(Brain.class)
public class Brain_memoriesMixin<E extends LivingEntity> implements BrainMemoryInterface<E> {

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;

    @Shadow
    @Final
    private Map<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>> tasks;

    @Shadow
    @Final
    private Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;

    @Shadow
    @Final
    private Set<Activity> possibleActivities;


    @Override
    public Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> getAllMobMemories() {
        return memories;
    }

    @Override
    public Map<Integer, Map<Activity, Set<Task<? extends LivingEntity>>>> getAllMobTasks() {
        return tasks;
    }

    @Override
    public Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> getAllMobSensors() {
        return sensors;
    }

    @Override
    public Set<Activity> getAllMobPossibleActivities() {
        return possibleActivities;
    }
}
