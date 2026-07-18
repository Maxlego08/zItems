package fr.traqueur.items.infrastructure.registry;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.items.ItemProvider;
import fr.traqueur.items.api.registries.ItemProviderRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Implementation of ItemProviderRegistry that manages item providers
 * from various plugin sources.
 */
public class ZItemProviderRegistry implements ItemProviderRegistry {

    private final Map<String, ItemProvider> providers;

    public ZItemProviderRegistry() {
        this.providers = new LinkedHashMap<>();
    }

    @Override
    public void register(String name, ItemProvider provider) {
        this.providers.put(name.toLowerCase(), provider);
        Logger.info("Registered item provider: {}", name);
    }

    @Override
    public ItemProvider getById(String name) {
        return this.providers.get(name.toLowerCase());
    }

    @Override
    public Collection<ItemProvider> getAll() {
        return this.providers.values();
    }

    @Override
    public void clear() {
        this.providers.clear();
        Logger.debug("Cleared all item providers.");
    }

    @Override
    public @NotNull Optional<ItemStack> createItem(@NotNull String providerName, @Nullable Player player, @NotNull String itemId) {
        ItemProvider provider = this.providers.get(providerName.toLowerCase());
        if (provider == null) {
            Logger.debug("Item provider '{}' not found", providerName);
            return Optional.empty();
        }

        Optional<ItemStack> item = provider.createItem(player, itemId);
        if (item.isPresent()) {
            Logger.debug("Created item '{}' from provider '{}'", itemId, providerName);
        }
        return item;
    }

    @Override
    public @NotNull Optional<ItemStack> createItem(@Nullable Player player, @NotNull String itemId) {
        for (Map.Entry<String, ItemProvider> entry : providers.entrySet()) {
            Optional<ItemStack> item = entry.getValue().createItem(player, itemId);
            if (item.isPresent()) {
                Logger.debug("Created item '{}' from provider '{}'", itemId, entry.getKey());
                return item;
            }
        }

        Logger.debug("No provider could create item '{}'", itemId);
        return Optional.empty();
    }

    @Override
    public boolean hasItem(@NotNull String itemId) {
        for (ItemProvider provider : providers.values()) {
            if (provider.hasItem(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasItem(@NotNull String providerName, @NotNull String itemId) {
        ItemProvider provider = this.providers.get(providerName.toLowerCase());
        return provider != null && provider.hasItem(itemId);
    }
}
