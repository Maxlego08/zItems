package fr.traqueur.items.entries.settings;

import fr.traqueur.items.api.entries.EntrySettings;

/**
 * Used by entries that need no configuration (e.g. ATTACK, KILL).
 */
public record EmptyEntrySettings() implements EntrySettings {
}
