package fr.traqueur.items.utils;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import fr.traqueur.items.api.utils.ItemUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for handling attribute modifiers on items.
 * Provides platform-specific implementations for Paper and Spigot.
 */
public final class AttributeUtil {

    private AttributeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Applies a list of attribute wrappers to an item stack with a specific merge strategy.
     * Uses the appropriate method based on the server platform (Paper or Spigot).
     *
     * @param itemStack the item stack to apply attributes to
     * @param attributes the list of attribute wrappers to apply
     * @param plugin the plugin instance
     * @param strategy the strategy to use when merging with existing attributes
     */
    public static void applyAttributes(ItemStack itemStack, List<AttributeWrapper> attributes, ItemsPlugin plugin, AttributeMergeStrategy strategy) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }

        if (PlatformType.isPaper()) {
            applyAttributesModern(itemStack, attributes, plugin, strategy);
        } else {
            applyAttributesLegacy(itemStack, attributes, plugin, strategy);
        }
    }

    /**
     * Applies attributes using Paper's DataComponent API with a merge strategy.
     *
     * @param itemStack the item stack to apply attributes to
     * @param attributes the list of attribute wrappers to apply
     * @param plugin the plugin instance
     * @param strategy the merge strategy to use
     */
    public static void applyAttributesModern(ItemStack itemStack, List<AttributeWrapper> attributes, ItemsPlugin plugin, AttributeMergeStrategy strategy) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        // Get existing modifiers
        ItemAttributeModifiers existing = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        List<AttributeEntry> existingEntries = new ArrayList<>();

        if (existing != null) {
            for (var entry : existing.modifiers()) {
                existingEntries.add(new AttributeEntry(entry.attribute(), entry.modifier()));
            }
        }

        // Create list of new modifiers
        List<AttributeEntry> newEntries = new ArrayList<>();
        for (AttributeWrapper wrapper : attributes) {
            AttributeModifier modifier = wrapper.toAttributeModifier(plugin);
            newEntries.add(new AttributeEntry(wrapper.attribute(), modifier));
        }

        // Apply strategy
        List<AttributeEntry> resultEntries = mergeAttributes(existingEntries, newEntries, strategy);

        // Build final attribute modifiers
        for (AttributeEntry entry : resultEntries) {
            builder.addModifier(entry.attribute, entry.modifier);
        }

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    /**
     * Applies attributes using Spigot's ItemMeta API with a merge strategy.
     *
     * @param itemStack the item stack to apply attributes to
     * @param attributes the list of attribute wrappers to apply
     * @param plugin the plugin instance
     * @param strategy the merge strategy to use
     */
    public static void applyAttributesLegacy(ItemStack itemStack, List<AttributeWrapper> attributes, ItemsPlugin plugin, AttributeMergeStrategy strategy) {
        ItemUtil.editMeta(itemStack, ItemMeta.class, meta -> {
            // Get existing modifiers
            List<AttributeEntry> existingEntries = new ArrayList<>();
            if (meta.hasAttributeModifiers()) {
                for (Attribute attr : Attribute.values()) {
                    var modifiers = meta.getAttributeModifiers(attr);
                    if (modifiers != null && !modifiers.isEmpty()) {
                        for (AttributeModifier modifier : modifiers) {
                            existingEntries.add(new AttributeEntry(attr, modifier));
                        }
                    }
                }
            }

            // Create list of new modifiers
            List<AttributeEntry> newEntries = new ArrayList<>();
            for (AttributeWrapper wrapper : attributes) {
                AttributeModifier modifier = wrapper.toAttributeModifier(plugin);
                newEntries.add(new AttributeEntry(wrapper.attribute(), modifier));
            }

            // Apply strategy
            List<AttributeEntry> resultEntries = mergeAttributes(existingEntries, newEntries, strategy);

            // Clear existing and apply merged modifiers
            for (Attribute attr : Attribute.values()) {
                meta.removeAttributeModifier(attr);
            }

            for (AttributeEntry entry : resultEntries) {
                meta.addAttributeModifier(entry.attribute, entry.modifier);
            }
        });
    }

    /**
     * Merges two attribute entry lists according to the specified strategy.
     *
     * @param existing list of existing attribute entries
     * @param newEntries list of new attribute entries to apply
     * @param strategy the merge strategy
     * @return the merged list of attribute entries
     */
    private static List<AttributeEntry> mergeAttributes(
            List<AttributeEntry> existing,
            List<AttributeEntry> newEntries,
            AttributeMergeStrategy strategy) {

        List<AttributeEntry> result = new ArrayList<>();

        switch (strategy) {
            case REPLACE -> {
                // Only keep new modifiers, discard all existing
                result.addAll(newEntries);
            }
            case ADD -> {
                // Keep all existing modifiers and add all new ones
                result.addAll(existing);
                result.addAll(newEntries);
            }
            case KEEP_HIGHEST -> {
                // For each (attribute, operation, slotGroup) triple, keep only the modifier with the highest value
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));

                // For each group, keep the one with highest amount
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        AttributeEntry highest = entries.stream()
                                .max(Comparator.comparingDouble(e -> e.modifier.getAmount()))
                                .orElse(entries.getFirst());
                        result.add(highest);
                    }
                }
            }
            case KEEP_LOWEST -> {
                // For each (attribute, operation, slotGroup) triple, keep only the modifier with the lowest value
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));

                // For each group, keep the one with lowest amount
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        AttributeEntry lowest = entries.stream()
                                .min(Comparator.comparingDouble(e -> e.modifier.getAmount()))
                                .orElse(entries.getFirst());
                        result.add(lowest);
                    }
                }
            }
            case SUM -> {
                // Group by (attribute, operation, slotGroup) and sum amounts
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));

                // For each group, sum all amounts
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (entries.isEmpty()) continue;

                    // Sum all amounts
                    double totalAmount = entries.stream()
                            .mapToDouble(e -> e.modifier.getAmount())
                            .sum();

                    // Create a new modifier with the summed amount
                    AttributeModifier first = entries.getFirst().modifier;
                    AttributeModifier summed = new AttributeModifier(
                            first.getKey(),
                            totalAmount,
                            first.getOperation(),
                            first.getSlotGroup()
                    );
                    result.add(new AttributeEntry(group.getKey().attribute, summed));
                }
            }
        }

        return result;
    }

    /**
     * Represents an attribute with its modifier.
     */
    private record AttributeEntry(Attribute attribute, AttributeModifier modifier) {}

    /**
     * Key for grouping modifiers by attribute, operation, and slot group.
     */
    private record ModifierKey(Attribute attribute, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup) {}
}