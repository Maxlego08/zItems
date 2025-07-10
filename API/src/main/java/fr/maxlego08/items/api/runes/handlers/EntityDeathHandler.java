package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.event.entity.EntityDeathEvent;

public interface EntityDeathHandler<T extends RuneConfiguration> {

    void onEntityDeath(ItemPlugin plugin, EntityDeathEvent event, T runeConfiguration);
}
