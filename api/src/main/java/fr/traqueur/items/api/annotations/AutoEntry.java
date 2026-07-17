package fr.traqueur.items.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an {@link fr.traqueur.items.api.entries.EntryHandler} for automatic registration.
 * The value is used as the discriminator key for polymorphic deserialization.
 *
 * <p>Mirrors {@link AutoEffect} — see that annotation for the equivalent mechanism on effects.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoEntry {

    /**
     * The unique identifier for this entry handler.
     * This will be used as the key in the entry handlers registry.
     *
     * @return the entry identifier
     */
    String value();
}
