package fr.traqueur.items.hooks.mythicmobs;

import fr.traqueur.items.api.entities.CustomEntityProvider;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MythicMobsProvider implements CustomEntityProvider {

    @Override
    public Optional<String> getCustomEntityId(Entity entity) {
        Optional<ActiveMob> activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        return activeMob.map(ActiveMob::getMobType);
    }
}
