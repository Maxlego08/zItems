package fr.traqueur.items.infrastructure.registry;

import fr.traqueur.items.api.effects.Applicator;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.registries.ApplicatorsRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the applicators registry.
 */
public class ZApplicatorsRegistry implements ApplicatorsRegistry {

    private final Map<String, Applicator> applicators = new HashMap<>();

    @Override
    public void register(String id, Applicator applicator) {
        applicators.put(id, applicator);
    }

    @Override
    public Applicator getById(String id) {
        return applicators.get(id);
    }

    @Override
    public Applicator getByEffect(Effect effect) {
        return getById(effect.id());
    }

    @Override
    public Collection<Applicator> getAll() {
        return applicators.values();
    }

    @Override
    public void clear() {
        applicators.clear();
    }
}