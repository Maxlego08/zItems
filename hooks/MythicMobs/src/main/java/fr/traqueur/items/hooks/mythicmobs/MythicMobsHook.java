package fr.traqueur.items.hooks.mythicmobs;

import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import fr.traqueur.items.api.registries.CustomEntityProviderRegistry;
import fr.traqueur.items.api.registries.Registry;

@AutoHook("MythicMobs")
public class MythicMobsHook implements Hook {

    @Override
    public void onEnable() {
        Registry.get(CustomEntityProviderRegistry.class).register("mythicmobs", new MythicMobsProvider());
    }
}
