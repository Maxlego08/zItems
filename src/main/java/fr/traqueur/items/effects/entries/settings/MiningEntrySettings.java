package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.items.effects.entries.BlockMatch;
import fr.traqueur.structura.annotations.Options;

import java.util.List;

/**
 * @param materials optional whitelist of {@link BlockMatch} references. Empty/absent
 *                  matches any block (equivalent to the old unfiltered BLOCK_BREAK).
 */
public record MiningEntrySettings(
        @Options(optional = true) List<BlockMatch> materials
) implements EntrySettings {
}
