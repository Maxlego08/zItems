package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.meta.AttributeConfiguration;
import fr.maxlego08.items.api.enchantments.EssentialsEnchantment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuneAttributeConfiguration extends RuneConfiguration {

    private List<AttributeConfiguration> attributes;

    public RuneAttributeConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.attributes = configuration.getMapList("attributes").stream().map(map -> {
            Attribute attribute = Attribute.valueOf(((String) map.get("attribute")).toUpperCase());
            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(((String) map.get("operation")).toUpperCase());
            double amount = ((Number) map.get("amount")).doubleValue();
            EquipmentSlotGroup slot = map.containsKey("slot") ? EquipmentSlotGroup.getByName((String) map.get("slot")) : EquipmentSlotGroup.ANY;

            return new AttributeConfiguration(attribute, operation, amount, slot);
        }).toList();

    }

    public List<AttributeConfiguration> getAttributes() {
        return attributes;
    }
}
