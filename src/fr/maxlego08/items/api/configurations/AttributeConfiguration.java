package fr.maxlego08.items.api.configurations;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public record AttributeConfiguration(Attribute attribute, AttributeModifier.Operation operation, double amount,
                                     EquipmentSlotGroup slot) {
}