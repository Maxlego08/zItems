package fr.traqueur.items.api.entries;

import fr.traqueur.items.api.effects.EffectContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Tests whether a pipeline should run, given the current {@link EffectContext}.
 * <p>
 * Kept deliberately separate from {@link fr.traqueur.items.api.effects.EffectHandler}:
 * an entry only ever tests a condition, it never acts — so it never needs a no-op
 * {@code handle()} implementation just to satisfy that contract.
 *
 * @param <T> the type of entry settings
 */
public interface EntryHandler<T extends EntrySettings> {

    /**
     * Tests whether the given context satisfies this entry's condition.
     *
     * @param context  the context in which the pipeline is being evaluated
     * @param settings the settings for this entry
     * @return true if the pipeline should proceed, false otherwise
     */
    boolean test(EffectContext context, T settings);

    /**
     * Returns the settings class type for this entry handler.
     * <p>
     * By default, this method uses reflection to extract the generic type parameter
     * from the implementing class, eliminating the need for manual implementation.
     *
     * @return the Class object for the settings type
     */
    @SuppressWarnings("unchecked")
    default Class<T> settingsType() {
        Type[] genericInterfaces = getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class<?> rawClass && EntryHandler.class.isAssignableFrom(rawClass)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        return (Class<T>) actualTypeArguments[0];
                    }
                }
            }
        }

        throw new IllegalStateException(
                "Could not resolve settings type for entry handler " + getClass().getName() +
                        ". Ensure the class directly implements EntryHandler with a concrete type parameter."
        );
    }
}
