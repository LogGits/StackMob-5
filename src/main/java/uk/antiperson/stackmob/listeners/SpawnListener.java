package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

public class SpawnListener implements Listener {

    private StackMob sm;
    public SpawnListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        sm.getServer().getScheduler().runTask(sm, () -> {
            if (sm.getMainConfig().isEntityBlacklisted(event.getEntity(), event.getSpawnReason())) {
                return;
            }
            if (sm.getEntityManager().isStackedEntity(event.getEntity())) {
                return;
            }
            StackEntity original = sm.getEntityManager().getStackEntity(event.getEntity());
            if (original.shouldWait(event.getSpawnReason())) {
                original.makeWait();
                return;
            }
            Integer[] searchRadius = sm.getMainConfig().getStackRadius(event.getEntity().getType());
            for (Entity entity : event.getEntity().getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                if (!(entity instanceof Mob)) {
                    continue;
                }
                StackEntity nearby = sm.getEntityManager().getStackEntity((LivingEntity) entity);
                if (!nearby.isCandidate()) {
                    continue;
                }
                if (sm.getMainConfig().getStackThresholdEnabled(entity.getType()) && nearby.getSize() == 1) {
                    continue;
                }
                if (nearby.merge(original)) {
                    return;
                }
            }
            original.setSize(1);
            sm.getHookManager().onSpawn(original);
            if (sm.getMainConfig().isAiDisabled(event.getEntity().getType(), event.getSpawnReason())) {
                event.getEntity().setAI(false);
            }
        });
    }
}
