package fr.traqueur.items.registries;

import fr.traqueur.items.ZItems;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.Applicator;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectRepresentation;
import fr.traqueur.items.api.registries.ApplicatorsRegistry;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.settings.models.IngredientWrapper;
import fr.traqueur.items.effects.engine.ZEffect;
import fr.traqueur.recipes.api.domains.Ingredient;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ZEffectsRegistry extends FileBasedRegistry<String, Effect> implements EffectsRegistry {

    public ZEffectsRegistry(ItemsPlugin plugin) {
        super(plugin, ZItems.EFFECTS_FOLDER, "Effects Registry");
    }


    @Override
    protected Effect loadFile(Path file) {
        try {
            Effect effect = Structura.load(file, ZEffect.class);
            this.register(effect.id(), effect);
            Logger.debug("Loaded effect: " + effect.id() + " from file: " + file.getFileName());

            // Create applicator if effect has representation
            registerApplicator(effect);

            return effect;
        } catch (StructuraException e) {
            Logger.severe("Failed to load effect from file: " + file.getFileName(), e);
            return null;
        }
    }

    /**
     * Registers an applicator for the effect if it has a representation.
     *
     * @param effect the effect to register an applicator for
     */
    private void registerApplicator(Effect effect) {
        if (effect == null || effect.representation() == null) {
            return;
        }

        EffectRepresentation representation = effect.representation();

        // Convert ingredient wrappers to actual ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        if (representation.ingredients() != null) {
            ingredients = representation.ingredients().stream()
                    .map(IngredientWrapper::toIngredient)
                    .toList();
        }

        // Create and register the applicator
        Applicator applicator = new Applicator(effect, ingredients, representation.applicatorType());

        ApplicatorsRegistry applicatorsRegistry = Registry.get(ApplicatorsRegistry.class);
        if (applicatorsRegistry != null) {
            applicatorsRegistry.register(effect.id(), applicator);
            Logger.debug("Registered applicator for effect: {}", effect.id());
        }
    }
}
