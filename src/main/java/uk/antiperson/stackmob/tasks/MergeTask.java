package uk.antiperson.stackmob.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.HashSet;
import java.util.Set;

public class MergeTask extends BukkitRunnable {

    private StackMob sm;
    public MergeTask(StackMob sm) {
        this.sm = sm;
    }

    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getEntitiesByClass(Mob.class)) {
                StackEntity original = sm.getEntityManager().getStackEntity(entity);
                if (!sm.getEntityManager().isStackedEntity(entity)) {
                    if (sm.getEntityManager().isWaiting(original.getEntity())) {
                        original.incrementWait();
                    }
                    continue;
                }
                if (!original.isCandidate()) {
                    continue;
                }
                Integer[] searchRadius = sm.getMainConfig().getStackRadius(entity.getType());
                Set<StackEntity> matches = new HashSet<>();
                for (Entity nearby : entity.getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                    if (!(nearby instanceof Mob)) {
                        continue;
                    }
                    if (!sm.getEntityManager().isStackedEntity(entity)) {
                        continue;
                    }
                    StackEntity nearbyStack = sm.getEntityManager().getStackEntity((LivingEntity) nearby);
                    if (!nearbyStack.isCandidate()) {
                        continue;
                    }
                    if (nearbyStack.getSize() > 1 || original.getSize() > 1) {
                        if (nearbyStack.merge(original)) {
                            break;
                        }
                    }
                    if (nearbyStack.canMerge(original)) {
                        matches.add(nearbyStack);
                    }
                }
                if (sm.getMainConfig().getStackThresholdEnabled(entity.getType())) {
                    int threshold = sm.getMainConfig().getStackThreshold(entity.getType()) - 1;
                    if (matches.size() >= threshold) {
                        original.incrementSize(matches.size());
                        matches.forEach(StackEntity::remove);
                    }
                }
            }
        }
    }
}
