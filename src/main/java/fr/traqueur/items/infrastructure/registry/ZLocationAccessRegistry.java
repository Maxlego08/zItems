package fr.traqueur.items.infrastructure.registry;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.blocks.LocationAccess;
import fr.traqueur.items.api.registries.LocationAccessRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZLocationAccessRegistry implements LocationAccessRegistry {

    private final Map<String, LocationAccess> accessors;

    public ZLocationAccessRegistry() {
        this.accessors = new HashMap<>();
    }

    @Override
    public void register(String s, LocationAccess item) {
        this.accessors.put(s, item);
    }

    @Override
    public LocationAccess getById(String s) {
        return this.accessors.get(s);
    }

    @Override
    public Collection<LocationAccess> getAll() {
        return this.accessors.values();
    }

    @Override
    public void clear() {
        this.accessors.clear();
        Logger.debug("Cleared all custom block providers.");
    }
}
