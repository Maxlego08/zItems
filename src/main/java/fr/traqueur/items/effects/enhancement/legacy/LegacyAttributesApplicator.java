package fr.traqueur.items.effects.enhancement.legacy;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.settings.AttributesSettings;
import fr.traqueur.items.utils.AttributeUtil;

@AutoEffect(value = "ATTRIBUTES_APPLICATOR")
@SpigotOnly
public record LegacyAttributesApplicator(
        ItemsPlugin plugin) implements EffectHandler.NoEventEffectHandler<AttributesSettings> {

    @Override
    public void handle(EffectContext context, AttributesSettings settings) {
        AttributeUtil.applyAttributesLegacy(context.itemSource(), settings.attributes(), plugin, settings.strategy());
    }

    @Override
    public int priority() {
        return 1;
    }
}