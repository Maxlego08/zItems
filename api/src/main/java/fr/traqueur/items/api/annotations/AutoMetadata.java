package fr.traqueur.items.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an {@link fr.traqueur.items.api.items.ItemMetadata} for automatic registration.
 * The value is used as the discriminator key for polymorphic deserialization.
 *
 * <p>Use {@link PaperOnly} / {@link SpigotOnly} for platform filtering,
 * and {@link SinceVersion} / {@link UntilVersion} for version-range filtering.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoMetadata {

    /**
     * The unique identifier for this metadata type.
     * This will be used as the discriminator key in polymorphic deserialization.
     *
     * @return the metadata identifier
     */
    String value();

}
