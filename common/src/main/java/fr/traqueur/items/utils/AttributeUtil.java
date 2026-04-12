package fr.traqueur.items.utils;

import fr.traqueur.items.api.services.AttributeApplicatorService;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Utility class for handling attribute modifiers on items.
 * Provides platform-specific implementations for Paper and Spigot.
 */
public final class AttributeUtil {

    /**
     * Paper DataComponent implementation, discovered via ServiceLoader.
     * Null on Spigot or Paper versions that don't provide the service.
     */
    private static final AttributeApplicatorService PAPER_SERVICE;
    static {
        Iterator<AttributeApplicatorService> it = ServiceLoader.load(AttributeApplicatorService.class).iterator();
        AttributeApplicatorService found = it.hasNext() ? it.next() : null;
        PAPER_SERVICE = PlatformType.isPaper() ? found : null;
    }

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

        if (PAPER_SERVICE != null) {
            PAPER_SERVICE.applyAttributes(itemStack, attributes, plugin, strategy);
        } else {
            applyAttributesLegacy(itemStack, attributes, plugin, strategy);
        }
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

            List<AttributeEntry> newEntries = new ArrayList<>();
            for (AttributeWrapper wrapper : attributes) {
                AttributeModifier modifier = wrapper.toAttributeModifier(plugin);
                newEntries.add(new AttributeEntry(wrapper.attribute(), modifier));
            }

            List<AttributeEntry> resultEntries = mergeAttributes(existingEntries, newEntries, strategy);

            for (Attribute attr : Attribute.values()) {
                meta.removeAttributeModifier(attr);
            }

            for (AttributeEntry entry : resultEntries) {
                meta.addAttributeModifier(entry.attribute, entry.modifier);
            }
        });
    }

    private static List<AttributeEntry> mergeAttributes(
            List<AttributeEntry> existing,
            List<AttributeEntry> newEntries,
            AttributeMergeStrategy strategy) {

        List<AttributeEntry> result = new ArrayList<>();

        switch (strategy) {
            case REPLACE -> result.addAll(newEntries);
            case ADD -> {
                result.addAll(existing);
                result.addAll(newEntries);
            }
            case KEEP_HIGHEST -> {
                List<AttributeEntry> allEntries = new ArrayList<>(existing);
                allEntries.addAll(newEntries);
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        entries.stream()
                                .max(Comparator.comparingDouble(e -> e.modifier.getAmount()))
                                .ifPresent(result::add);
                    }
                }
            }
            case KEEP_LOWEST -> {
                List<AttributeEntry> allEntries = new ArrayList<>(existing);
                allEntries.addAll(newEntries);
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        entries.stream()
                                .min(Comparator.comparingDouble(e -> e.modifier.getAmount()))
                                .ifPresent(result::add);
                    }
                }
            }
            case SUM -> {
                List<AttributeEntry> allEntries = new ArrayList<>(existing);
                allEntries.addAll(newEntries);
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream()
                        .collect(Collectors.groupingBy(entry -> new ModifierKey(
                                entry.attribute,
                                entry.modifier.getOperation(),
                                entry.modifier.getSlotGroup()
                        )));
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (entries.isEmpty()) continue;
                    double totalAmount = entries.stream().mapToDouble(e -> e.modifier.getAmount()).sum();
                    AttributeModifier first = entries.getFirst().modifier;
                    result.add(new AttributeEntry(group.getKey().attribute,
                            new AttributeModifier(first.getKey(), totalAmount, first.getOperation(), first.getSlotGroup())));
                }
            }
        }

        return result;
    }

    private record AttributeEntry(Attribute attribute, AttributeModifier modifier) {}
    private record ModifierKey(Attribute attribute, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup) {}
}