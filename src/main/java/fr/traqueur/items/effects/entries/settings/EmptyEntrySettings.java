package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;

/**
 * Used by entries that need no configuration (e.g. ATTACK, KILL).
 */
public record EmptyEntrySettings() implements EntrySettings {
}
