package fr.traqueur.items.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Bukkit {@link org.bukkit.event.Listener} for automatic discovery and registration.
 * Classes annotated with this are scanned at startup and registered via
 * {@code Bukkit.getPluginManager().registerEvents(...)}.
 * <p>
 * Respects {@link PaperOnly}, {@link SpigotOnly}, {@link SinceVersion}, and {@link UntilVersion}
 * for conditional registration.
 *
 * <pre>{@code
 * @AutoListener
 * @PaperOnly
 * @SinceVersion("1.21.4")
 * public class MyPaperListener implements Listener { ... }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoListener {
}