package fr.traqueur.items.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an {@link fr.traqueur.items.api.effects.exit.ExitHandler} for automatic registration.
 * The value is used as the discriminator key for polymorphic deserialization.
 *
 * <p>Mirrors {@link AutoEffect}/{@link AutoEntry} — see those annotations for the equivalent
 * mechanism on effects/entries.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoExit {

    /**
     * The unique identifier for this exit handler.
     * This will be used as the key in the exit handlers registry.
     *
     * @return the exit identifier
     */
    String value();
}
