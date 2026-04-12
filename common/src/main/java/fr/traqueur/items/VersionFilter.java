package fr.traqueur.items;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.MinecraftVersion;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.annotations.UntilVersion;

/**
 * Centralized filter that decides whether a class should be registered on the current server.
 *
 * <p>Checks two independent dimensions:
 * <ol>
 *   <li><b>Platform</b> — {@link PaperOnly} / {@link SpigotOnly}</li>
 *   <li><b>Version range</b> — {@link SinceVersion} / {@link UntilVersion}</li>
 * </ol>
 *
 * <p>Used by all registry scanning loops in place of ad-hoc platform checks.
 * Annotations can be freely combined, e.g. {@code @PaperOnly @SinceVersion("1.21.5")}.
 */
public final class VersionFilter {

    private VersionFilter() {}

    /**
     * Returns {@code true} if the given class should be registered on the current server.
     *
     * @param clazz the candidate implementation class
     * @param label a human-readable name for log messages (typically {@code clazz.getSimpleName()})
     * @return {@code true} if all platform and version constraints are satisfied
     */
    public static boolean passes(Class<?> clazz, String label) {
        // --- Dimension 1: Platform ---
        if (clazz.isAnnotationPresent(PaperOnly.class) && !PlatformType.isPaper()) {
            Logger.debug("Skipping <aqua>{}<reset> — @PaperOnly, running Spigot.", label);
            return false;
        }
        if (clazz.isAnnotationPresent(SpigotOnly.class) && PlatformType.isPaper()) {
            Logger.debug("Skipping <aqua>{}<reset> — @SpigotOnly, running Paper.", label);
            return false;
        }

        // --- Dimension 2: Version range ---
        MinecraftVersion server = MinecraftVersion.current();

        SinceVersion since = clazz.getAnnotation(SinceVersion.class);
        if (since != null) {
            MinecraftVersion minimum = MinecraftVersion.parse(since.value());
            if (!server.isAtLeast(minimum)) {
                Logger.debug("Skipping <aqua>{}<reset> — requires >= <gold>{}<reset>, server is <gold>{}<reset>.",
                        label, since.value(), server);
                return false;
            }
        }

        UntilVersion until = clazz.getAnnotation(UntilVersion.class);
        if (until != null) {
            MinecraftVersion maximum = MinecraftVersion.parse(until.value());
            if (!server.isAtMost(maximum)) {
                Logger.debug("Skipping <aqua>{}<reset> — requires <= <gold>{}<reset>, server is <gold>{}<reset>.",
                        label, until.value(), server);
                return false;
            }
        }

        return true;
    }
}
