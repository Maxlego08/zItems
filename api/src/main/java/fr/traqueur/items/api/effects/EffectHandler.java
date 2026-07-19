package fr.traqueur.items.api.effects;

import fr.traqueur.items.api.annotations.IncompatibleWith;
import org.bukkit.event.Event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an effect that can be applied in response to a specific event.
 * Effects can be multi-event or single-event.
 * This interface defines the contract for effects that can be registered and executed.
 * @param <T> the type of effect settings
 */
public sealed interface EffectHandler<T extends EffectSettings> permits EffectHandler.MultiEventEffectHandler, EffectHandler.NoEventEffectHandler, EffectHandler.SingleEventEffectHandler, EffectHandler.AnyEventEffectHandler {

    /**
     * Handles the effect application logic.
     *
     * @param context  the context in which the effect is applied
     * @param settings the settings for this effect
     */
    void handle(EffectContext context, T settings);

    /**
     * Returns the priority of this effect handler.
     * Handlers with higher priority values are executed before those with lower values.
     *
     * @return the priority of the effect handler
     */
    int priority();

    /**
     * Returns the settings class type for this handler.
     * <p>
     * By default, this method uses reflection to extract the generic type parameter
     * from the implementing class, eliminating the need for manual implementation.
     * <p>
     * Handlers can override this method if needed, but it's generally not necessary.
     *
     * @return the Class object for the settings type
     */
    @SuppressWarnings("unchecked")
    default Class<T> settingsType() {
        // Try to get from generic interfaces
        Type[] genericInterfaces = getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                Type rawType = parameterizedType.getRawType();
                // Check if this is one of our handler interfaces
                if (rawType instanceof Class<?> rawClass &&
                        EffectHandler.class.isAssignableFrom(rawClass)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        return (Class<T>) actualTypeArguments[0];
                    }
                }
            }
        }

        throw new IllegalStateException(
                "Could not resolve settings type for handler " + getClass().getName() +
                        ". Ensure the class directly implements one of the EffectHandler interfaces with concrete type parameters."
        );
    }

    /**
     * Determines if this effect handler can be applied to the given event.
     *
     * @param event the event to check
     * @return true if the handler can be applied to the event, false otherwise
     */
    default boolean canApply(Event event) {
        return switch (this) {
            case SingleEventEffectHandler<?, ?> singleEventEffectHandler ->
                    singleEventEffectHandler.eventType().isInstance(event);
            case MultiEventEffectHandler<?> multiEventEffectHandler ->
                    multiEventEffectHandler.eventTypes().stream().anyMatch(type -> type.isInstance(event));
            case NoEventEffectHandler<?> __ -> event == null;
            case AnyEventEffectHandler<?> __ -> true;
        };
    }

    /**
     * Returns a set of effect handler classes that are incompatible with this handler.
     * This is determined by the {@link IncompatibleWith} annotation on the implementing class.
     *
     * @return set of incompatible effect handler classes, or empty set if none
     */
    default Set<Class<? extends EffectHandler<?>>> getIncompatibleHandlers() {
        IncompatibleWith annotation = this.getClass().getAnnotation(IncompatibleWith.class);
        if (annotation == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(annotation.value())
                .collect(Collectors.toSet());
    }

    /**
     * An effect handler that can handle multiple event types.
     *
     * @param <T> the type of effect settings
     */
    non-sealed interface MultiEventEffectHandler<T extends EffectSettings> extends EffectHandler<T> {
        /**
         * Returns the set of event types this handler can respond to.
         *
         * @return set of event classes
         */
        Set<Class<? extends Event>> eventTypes();
    }

    /**
     * An effect handler that handles a single specific event type.
     *
     * @param <T> the type of effect settings
     * @param <E> the type of event this handler responds to
     */
    non-sealed interface SingleEventEffectHandler<T extends EffectSettings, E extends Event> extends EffectHandler<T> {

        /**
         * Returns the event type this handler responds to.
         * <p>
         * By default, this method uses reflection to extract the generic type parameter
         * from the implementing class, eliminating the need for manual implementation.
         * <p>
         * Handlers can override this method if needed, but it's generally not necessary.
         *
         * @return the Class object for the event type
         */
        @SuppressWarnings("unchecked")
        default Class<E> eventType() {
            Type[] genericInterfaces = getClass().getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType parameterizedType) {
                    Type rawType = parameterizedType.getRawType();
                    // Check if this is one of our handler interfaces
                    if (rawType instanceof Class<?> rawClass &&
                            SingleEventEffectHandler.class.isAssignableFrom(rawClass)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0) {
                            return (Class<E>) actualTypeArguments[1];
                        }
                    }
                }
            }

            throw new IllegalStateException(
                    "Could not resolve event type for handler " + getClass().getName() +
                            ". Ensure the class directly implements one of the EffectHandler interfaces with concrete type parameters."
            );
        }
    }

    /**
     * An effect handler that does not respond to any event.
     *
     * @param <T> the type of effect settings
     */
    non-sealed interface NoEventEffectHandler<T extends EffectSettings> extends EffectHandler<T> {
    }

    /**
     * An effect handler that can be applied regardless of the triggering event.
     * <p>
     * Unlike {@link SingleEventEffectHandler}/{@link MultiEventEffectHandler}, this handler
     * does not declare any event affinity of its own — {@link #canApply(Event)} always
     * returns {@code true}. Filtering responsibility is delegated to whatever this handler
     * consults internally (e.g. a pipeline effect delegating to its configured entry).
     * <p>
     * Because it declares no event type, {@link #canApply(Event)} being unconditionally
     * true means a handler of this kind must never be registered without something else
     * (like a pipeline's entry) actually gating its behavior — otherwise it would run on
     * every single dispatched event.
     *
     * @param <T> the type of effect settings
     */
    non-sealed interface AnyEventEffectHandler<T extends EffectSettings> extends EffectHandler<T> {
    }

}