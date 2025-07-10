package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractionHandler<T extends RuneConfiguration> {

    void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, T runeConfiguration);

}
