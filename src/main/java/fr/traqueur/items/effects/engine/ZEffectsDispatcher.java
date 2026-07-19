package fr.traqueur.items.effects.engine;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.*;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.serialization.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZEffectsDispatcher implements EffectsDispatcher {

    @Override
    public EffectContext applyNoEventEffects(Player player, ItemStack itemSource) {
        return dispatch(player, itemSource, null);
    }

    @Override
    public void applyNoEventEffect(Player player, ItemStack itemSource, Effect effect) {
        EffectContext context = new EffectContext(
                player,
                itemSource,
                null,
                new HashSet<>(), // affectedBlocks
                new ArrayList<>() // drops
        );
        EffectHandler<?> handler = Registry.get(HandlersRegistry.class).getById(effect.type());
        if (handler != null) {
            // Only execute if this is a NoEventHandler (canApply returns true for null event)
            if (!handler.canApply(null)) {
                Logger.debug("Handler {} is not a NoEventHandler, skipping",
                        handler.getClass().getSimpleName());
                return;
            }
            try {
                executeHandler(handler, context, effect.settings());
            } catch (Exception e) {
                Logger.severe("Error executing handler <red>{}<reset> for effect <yellow>{}",
                        e, handler.getClass().getSimpleName(), effect.id());
            }
        }
    }

    @Override
    public EffectContext dispatch(Player player, ItemStack itemSource, Event event) {
        // 1. Retrieve effects from the item's PDC
        List<Effect> effects = Keys.EFFECTS.get(itemSource.getItemMeta().getPersistentDataContainer(), new ArrayList<>());
        if (effects == null || effects.isEmpty()) {
            return null; // No effects to process
        }

        Logger.debug("Dispatching {} effect(s) for event <aqua>{}<reset>",
                effects.size(), event != null ? event.getEventName() : "NoEvent");

        // 2. Create the shared context
        EffectContext context = new EffectContext(
                player,
                itemSource,
                event,
                new HashSet<>(), // affectedBlocks
                new ArrayList<>() // drops
        );

        // 3. Collect all applicable handlers
        List<HandlerExecution> executions = collectApplicableHandlers(effects, event);
        if (executions.isEmpty()) {
            Logger.debug("No applicable handlers found for event {}",
                    event != null ? event.getEventName() : "NoEvent");
            return context;
        }

        // 4. Sort handlers by priority (descending order - highest priority first)
        executions.sort(Comparator.comparingInt((HandlerExecution e) -> e.handler.priority()).reversed());

        Logger.debug("Executing {} handler(s) in priority order", executions.size());

        // 5. Execute the pipeline
        for (HandlerExecution execution : executions) {
            try {
                executeHandler(execution.handler, context, execution.settings);
            } catch (Exception e) {
                Logger.severe("Error executing handler <red>{}<reset> for effect <yellow>{}<reset>: {}",
                        e, execution.handler.getClass().getSimpleName(), execution.effectId);
            }
        }

        return context;
    }

    /**
     * Collects all handlers that are applicable to the given event.
     * <p>
     * A handler is applicable if:
     * <ul>
     *   <li>A handler exists for the effect ID</li>
     *   <li>The handler's {@link EffectHandler#canApply(Event)} returns true</li>
     * </ul>
     *
     * @param effects the list of effects from the item
     * @param event   the event to check applicability against
     * @return a list of handler executions
     */
    private List<HandlerExecution> collectApplicableHandlers(List<Effect> effects, Event event) {
        List<HandlerExecution> executions = new ArrayList<>();

        for (Effect effect : effects) {
            // Get the handler for this effect
            EffectHandler<?> handler = Registry.get(HandlersRegistry.class).getById(effect.type());

            if (handler == null) {
                Logger.warning("No handler found for effect ID: <yellow>{}<reset> of type {}", effect.id(), effect.type());
                continue;
            }

            // Check if the handler can apply to this event
            if (!handler.canApply(event)) {
                Logger.debug("Handler {} cannot apply to event {}",
                        handler.getClass().getSimpleName(), event != null ? event.getEventName() : "NoEvent");
                continue;
            }

            executions.add(new HandlerExecution(effect.id(), handler, effect.settings()));
        }

        return executions;
    }

    /**
     * Executes a single handler with type-safe settings.
     *
     * @param handler  the handler to execute
     * @param context  the shared context
     * @param settings the settings for this effect
     * @param <T>      the settings type
     */
    private <T extends EffectSettings> void executeHandler(
            EffectHandler<T> handler,
            EffectContext context,
            EffectSettings settings) {

        Logger.debug("Executing handler: <aqua>{}<reset> [priority={}]",
                handler.getClass().getSimpleName(), handler.priority());

        if (!handler.settingsType().isInstance(settings)) {
            Logger.severe("Settings type mismatch for handler {}: expected {}, got {}",
                    handler.getClass().getSimpleName(),
                    handler.settingsType().getSimpleName(),
                    settings.getClass().getSimpleName());
            return;
        }

        handler.handle(context, handler.settingsType().cast(settings));
    }

    /**
     * Internal record to hold handler execution data.
     *
     * @param effectId the ID of the effect
     * @param handler  the handler to execute
     * @param settings the settings for this execution
     */
    private record HandlerExecution(
            String effectId,
            EffectHandler<?> handler,
            EffectSettings settings
    ) {
    }
}