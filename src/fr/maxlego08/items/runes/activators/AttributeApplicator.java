package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneAttributeConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.UUID;

public class AttributeApplicator extends RuneActivatorHelper<RuneAttributeConfiguration> {

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, RuneAttributeConfiguration runeConfiguration) throws Exception {
        runeConfiguration.getAttributes()
                .forEach(attributeConfiguration -> itemMeta.addAttributeModifier(
                        attributeConfiguration.attribute(),
                        new AttributeModifier(Objects.requireNonNull(NamespacedKey.fromString(UUID.randomUUID().toString(), plugin)),
                                attributeConfiguration.amount(),
                                attributeConfiguration.operation(),
                                attributeConfiguration.slot())));
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
