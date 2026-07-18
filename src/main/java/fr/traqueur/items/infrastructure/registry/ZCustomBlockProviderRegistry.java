package fr.traqueur.items.infrastructure.registry;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.blocks.CustomBlockProvider;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Implementation of CustomBlockProviderRegistry that iterates through
 * registered providers to find custom block drops.
 */
public class ZCustomBlockProviderRegistry implements CustomBlockProviderRegistry {

    private final Map<String, CustomBlockProvider> providers;

    public ZCustomBlockProviderRegistry() {
        this.providers = new LinkedHashMap<>();
    }

    @Override
    public void register(String name, CustomBlockProvider provider) {
        this.providers.put(name, provider);
        Logger.info("Registered custom block provider: {}", name);
    }

    @Override
    public CustomBlockProvider getById(String name) {
        return this.providers.get(name);
    }

    @Override
    public Collection<CustomBlockProvider> getAll() {
        return this.providers.values();
    }

    @Override
    public void clear() {
        this.providers.clear();
        Logger.debug("Cleared all custom block providers.");
    }

    @Override
    public Optional<List<ItemStack>> getCustomBlockDrop(Block block, Player player) {
        // Iterate through all providers until one returns a drop
        for (Map.Entry<String, CustomBlockProvider> provider : providers.entrySet()) {
            Optional<List<ItemStack>> drop = provider.getValue().getCustomBlockDrop(block, player);
            if (drop.isPresent()) {
                Logger.debug("Custom block drop found via provider: {}", provider.getKey());
                return drop;
            }
        }

        // No provider recognized this block as custom
        return Optional.empty();
    }
}
