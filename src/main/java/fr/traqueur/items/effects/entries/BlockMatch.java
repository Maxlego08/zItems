package fr.traqueur.items.effects.entries;

import fr.traqueur.structura.api.Loadable;
import org.bukkit.block.Block;

/**
 * Wraps a single block reference — a plain vanilla name ({@code "STONE"}) or a
 * {@code provider:id} custom block reference ({@code "itemsadder:ruby_ore"}), resolved
 * through {@code CustomBlockProviderRegistry}. Mirrors {@code IngredientWrapper}'s
 * shape: a dedicated settings object per reference rather than a bare string.
 *
 * @param block the block reference
 */
public record BlockMatch(String block) implements Loadable {

    public boolean matches(Block target) {
        return CustomMatch.block(block, target);
    }
}
