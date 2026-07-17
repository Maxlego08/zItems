package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.items.effects.entries.EntityMatch;
import fr.traqueur.structura.annotations.Options;

import java.util.List;

/**
 * @param entities optional whitelist of {@link EntityMatch} references. Empty/absent
 *                 matches any entity.
 */
public record KillEntrySettings(
        @Options(optional = true) List<EntityMatch> entities
) implements EntrySettings {
}
