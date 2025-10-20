package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public interface BucketHandler<T extends RuneConfiguration> {

    /**
     * Called when a player empties a bucket (places water/lava).
     * Return true to cancel the bucket emptying (making it infinite).
     *
     * @param plugin           The item plugin instance
     * @param event            The bucket empty event
     * @param runeConfiguration The rune configuration
     */
    void onBucketEmpty(ItemPlugin plugin, PlayerBucketEmptyEvent event, T runeConfiguration);

    /**
     * Called when a player fills a bucket (picks up water/lava).
     * Return true to cancel the bucket filling (keeping it empty or keeping its current state).
     *
     * @param plugin           The item plugin instance
     * @param event            The bucket fill event
     * @param runeConfiguration The rune configuration
     */
    void onBucketFill(ItemPlugin plugin, PlayerBucketFillEvent event, T runeConfiguration);

}