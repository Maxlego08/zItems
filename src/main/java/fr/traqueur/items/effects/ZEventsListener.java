package fr.traqueur.items.effects;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.EffectsDispatcher;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import fr.traqueur.items.api.registries.ExtractorsRegistry;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic event listener that automatically registers Bukkit event listeners
 * based on the event types declared in registered {@link EffectHandler}s.
 * <p>
 * This listener uses reflection to:
 * <ol>
 *   <li>Scan all registered handlers to find which event types they handle</li>
 *   <li>Dynamically register a Bukkit listener for each unique event type</li>
 *   <li>Extract the ItemStack source using the ExtractorsRegistry</li>
 *   <li>Dispatch events to the {@link ZEffectsDispatcher}</li>
 * </ol>
 * <p>
 * Benefits of this approach:
 * <ul>
 *   <li>No manual @EventHandler methods needed</li>
 *   <li>Automatically supports new handlers without code changes</li>
 *   <li>Single listener per event type (efficient)</li>
 *   <li>Extensible via custom extractors</li>
 * </ul>
 */
public record ZEventsListener(EffectsDispatcher dispatcher) implements Listener {

    private static final ConcurrentHashMap<Class<? extends Event>, Method> HANDLER_LISTS_CACHE = new ConcurrentHashMap<>();

    /**
     * Creates a new dynamic events listener.
     *
     * @param dispatcher the dispatcher to send events to
     */
    public ZEventsListener {
    }

    /**
     * Scans all registered handlers and automatically creates Bukkit listeners
     * for each unique event type.
     * <p>
     * This method should be called after all handlers have been registered
     * in the {@link HandlersRegistry}.
     *
     * @param plugin the plugin instance to register listeners with
     */
    public void registerDynamicListeners(JavaPlugin plugin) {
        HandlersRegistry handlersRegistry = Registry.get(HandlersRegistry.class);
        Set<Class<? extends Event>> eventTypes = collectEventTypes(handlersRegistry);

        Logger.info("Registering dynamic listeners for <gold>{}<reset> event type(s)...", eventTypes.size());

        int registered = 0;
        for (Class<? extends Event> eventType : eventTypes) {
            if (registerListenerForEvent(plugin, eventType)) {
                registered++;
            }
        }

        Logger.info("Successfully registered <gold>{}<reset> dynamic event listener(s)", registered);
    }

    /**
     * Collects all event types declared in registered handlers.
     * <p>
     * Scans through all handlers and extracts:
     * <ul>
     *   <li>{@link EffectHandler.SingleEventEffectHandler#eventType()}</li>
     *   <li>{@link EffectHandler.MultiEventEffectHandler#eventTypes()}</li>
     * </ul>
     *
     * @param handlersRegistry the registry containing all registered handlers
     * @return a set of all unique event types
     */
    private Set<Class<? extends Event>> collectEventTypes(HandlersRegistry handlersRegistry) {
        Set<Class<? extends Event>> eventTypes = new HashSet<>();

        for (EffectHandler<?> handler : handlersRegistry.getAll()) {
            switch (handler) {
                case EffectHandler.SingleEventEffectHandler<?, ?> single -> eventTypes.add(single.eventType());

                case EffectHandler.MultiEventEffectHandler<?> multi -> eventTypes.addAll(multi.eventTypes());

                case EffectHandler.NoEventEffectHandler<?> __ -> {
                    // NoEventHandlers don't listen to events
                }
            }
        }

        return eventTypes;
    }

    /**
     * Dynamically registers a Bukkit listener for a specific event type.
     * <p>
     * Uses reflection to access the event's {@code getHandlerList()} method
     * and registers a {@link RegisteredListener} that delegates to our
     * {@link #handleEvent(Event)} method.
     *
     * @param plugin    the plugin to register the listener with
     * @param eventType the event class to listen for
     * @return true if registration succeeded, false otherwise
     */
    private boolean registerListenerForEvent(JavaPlugin plugin, Class<? extends Event> eventType) {
        // Check that an extractor exists for this event type
        if (!Registry.get(ExtractorsRegistry.class).has(eventType)) {
            Logger.warning("No ItemSourceExtractor for event type: <yellow>{}<reset>. Skipping listener registration.",
                    eventType.getSimpleName());
            return false;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (eventType.isInstance(event)) {
                    handleEvent(event);
                }
            };

            RegisteredListener registeredListener = new RegisteredListener(
                    this,
                    executor,
                    EventPriority.HIGHEST,
                    plugin,
                    false  // ignoreCancelled = false (process even cancelled events)
            );

            // Get the HandlerList for this event type and register our listener
            HandlerList handlerList = getHandlerList(eventType);
            handlerList.register(registeredListener);

            Logger.debug("Registered dynamic listener for event: <aqua>{}<reset>", eventType.getSimpleName());
            return true;

        } catch (Exception e) {
            Logger.severe("Failed to register listener for event <red>{}<reset>: {}",
                    e, eventType.getSimpleName());
            return false;
        }
    }

    /**
     * Retrieves the static {@code HandlerList} from an event class using reflection.
     * <p>
     * All Bukkit events have a static {@code getHandlerList()} method that returns
     * the {@link HandlerList} for that event type.
     *
     * @param eventClass the event class
     * @return the HandlerList for this event
     * @throws Exception if reflection fails
     */
    private HandlerList getHandlerList(Class<? extends Event> eventClass) throws Exception {
        return (HandlerList) HANDLER_LISTS_CACHE.computeIfAbsent(eventClass, cls -> {
            try {
                return cls.getMethod("getHandlerList");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Event class " + cls.getName() + " does not have getHandlerList method", e);
            }
        }).invoke(null);
    }

    /**
     * Handles an event by extracting the item source and dispatching to handlers.
     * <p>
     * Process:
     * <ol>
     *   <li>Get the appropriate {@link ItemSourceExtractor} for this event</li>
     *   <li>Extract the player and ItemStack from the event</li>
     *   <li>Validate the extraction result</li>
     *   <li>Dispatch to the {@link ZEffectsDispatcher}</li>
     * </ol>
     *
     * @param event the Bukkit event
     * @param <E>   the event type
     */
    @SuppressWarnings("unchecked")
    private <E extends Event> void handleEvent(E event) {
        if (event instanceof Cancellable cancellable) {
            if (cancellable.isCancelled()) {
                return;
            }
        }

        Class<E> eventClass = (Class<E>) event.getClass();
        ItemSourceExtractor<E> extractor = (ItemSourceExtractor<E>) Registry.get(ExtractorsRegistry.class).getById(eventClass);

        if (extractor == null) {
            // No extractor found (shouldn't happen if registerDynamicListeners worked correctly)
            return;
        }

        // Extract the item source
        ItemSourceExtractor.ExtractionResult result = extractor.extract(event);
        if (result == null || !result.isValid()) {
            // No valid item source (e.g., player broke block with empty hand)
            return;
        }

        // Dispatch to handlers
        EffectContext context = dispatcher.dispatch(result.player(), result.itemSource(), event);

        // If no effects were processed, context will be null
        if (context == null) {
            return;
        }

        // Break all affected blocks
        for (Block block : context.affectedBlocks()) {
            block.setType(Material.AIR);
        }

        // Drop all collected items
        for (ItemStack drop : context.drops()) {
            result.player().getWorld().dropItemNaturally(result.player().getLocation(), drop);
        }
    }
}