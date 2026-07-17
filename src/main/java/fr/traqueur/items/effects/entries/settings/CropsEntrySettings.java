package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.Material;

import java.util.List;

/**
 * @param materials optional whitelist of crop materials (e.g. {@code WHEAT}, {@code CARROTS}).
 *                   Empty/absent matches any mature crop.
 */
public record CropsEntrySettings(
        @Options(optional = true) List<Material> materials
) implements EntrySettings {
}
