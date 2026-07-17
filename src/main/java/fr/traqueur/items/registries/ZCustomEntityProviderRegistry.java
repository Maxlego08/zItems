package fr.traqueur.items.registries;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.entities.CustomEntityProvider;
import fr.traqueur.items.api.registries.CustomEntityProviderRegistry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of CustomEntityProviderRegistry. Mirrors {@link ZCustomBlockProviderRegistry}.
 */
public class ZCustomEntityProviderRegistry implements CustomEntityProviderRegistry {

    private final Map<String, CustomEntityProvider> providers;

    public ZCustomEntityProviderRegistry() {
        this.providers = new LinkedHashMap<>();
    }

    @Override
    public void register(String name, CustomEntityProvider provider) {
        this.providers.put(name, provider);
        Logger.info("Registered custom entity provider: {}", name);
    }

    @Override
    public CustomEntityProvider getById(String name) {
        return this.providers.get(name);
    }

    @Override
    public Collection<CustomEntityProvider> getAll() {
        return this.providers.values();
    }

    @Override
    public void clear() {
        this.providers.clear();
        Logger.debug("Cleared all custom entity providers.");
    }
}
