package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;

import java.util.List;

/**
 * @param entities optional whitelist — plain vanilla names ({@code "PIG"}) or
 *                 {@code provider:id} custom mob references ({@code "mythicmobs:my_boss"}).
 *                 Empty/absent matches any entity.
 */
public record KillEntrySettings(
        @Options(optional = true) List<String> entities
) implements EntrySettings {
}
