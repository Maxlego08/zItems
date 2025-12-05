package fr.traqueur.items.effects.handlers;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.settings.EmptySettings;
import org.bukkit.inventory.meta.ItemMeta;

@AutoEffect(value = "UNBREAKABLE")
public class Unbreakable implements EffectHandler.NoEventEffectHandler<EmptySettings> {

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        ItemMeta meta = context.itemSource().getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            context.itemSource().setItemMeta(meta);
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
