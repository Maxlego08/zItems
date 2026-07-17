package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;

import java.util.List;

/**
 * @param materials optional whitelist — plain vanilla names ({@code "STONE"}) or
 *                  {@code provider:id} custom block references ({@code "itemsadder:ruby_ore"}).
 *                  Empty/absent matches any block (equivalent to the old unfiltered BLOCK_BREAK).
 */
public record MiningEntrySettings(
        @Options(optional = true) List<String> materials
) implements EntrySettings {
}
