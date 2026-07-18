package fr.traqueur.items.infrastructure.support;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.blocks.LocationAccess;
import fr.traqueur.items.api.registries.LocationAccessRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.settings.Settings;
import fr.traqueur.items.infrastructure.settings.PluginSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.util.List;

public class EventUtil {

    public static boolean fireEvent(Event event, HandlerList handlers) {
        List<String> allowedPlugins = Settings.get(PluginSettings.class).blockBreakEventPlugins();
        if (allowedPlugins == null || allowedPlugins.isEmpty()) {
            return true;
        }
        RegisteredListener[] listeners = handlers.getRegisteredListeners();
        ;
        for (RegisteredListener listener : listeners) {
            if (!allowedPlugins.contains(listener.getPlugin().getName())) {
                continue;
            }
            try {
                listener.callEvent(event);
            } catch (Exception e) {
                Logger.severe("An error occurred while firing event " + event.getClass().getSimpleName() + " for plugin " + listener.getPlugin().getName(), e);
            }
        }
        if (event instanceof Cancellable cancellableEvent) {
            return !cancellableEvent.isCancelled();
        }
        return true;
    }

    /**
     * Checks if a player can break a block at the given location.
     * This method checks all registered LocationAccess hooks.
     *
     * @param player   the player attempting to break the block
     * @param location the location of the block
     * @return true if the player can break the block, false otherwise
     */
    public static boolean canBreakBlock(Player player, Location location) {
        LocationAccessRegistry registry = Registry.get(LocationAccessRegistry.class);
        // Check all registered location access hooks
        for (LocationAccess access : registry.getAll()) {
            if (!access.hasAccess(player, location)) {
                return false; // If any hook denies access, return false
            }
        }

        return true; // All hooks allow access
    }

}
