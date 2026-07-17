package fr.traqueur.items.api.exit;

import fr.traqueur.items.api.effects.EffectContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Resolves what happens to a pipeline's collected drops once its steps are done.
 * <p>
 * Kept separate from {@link fr.traqueur.items.api.effects.EffectHandler}/{@link fr.traqueur.items.api.entries.EntryHandler}:
 * an exit only ever acts on the shared {@link EffectContext} at the end of a pipeline run.
 *
 * @param <T> the type of exit settings
 */
public interface ExitHandler<T extends ExitSettings> {

    /**
     * Resolves this exit against the given context (e.g. sell/drop remaining items).
     *
     * @param context  the context accumulated by the pipeline's steps
     * @param settings the settings for this exit
     */
    void resolve(EffectContext context, T settings);

    /**
     * Returns the settings class type for this exit handler.
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
                if (rawType instanceof Class<?> rawClass && ExitHandler.class.isAssignableFrom(rawClass)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        return (Class<T>) actualTypeArguments[0];
                    }
                }
            }
        }

        throw new IllegalStateException(
                "Could not resolve settings type for exit handler " + getClass().getName() +
                        ". Ensure the class directly implements ExitHandler with a concrete type parameter."
        );
    }
}
