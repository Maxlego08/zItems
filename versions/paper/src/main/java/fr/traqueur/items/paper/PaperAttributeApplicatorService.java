package fr.traqueur.items.paper;

import fr.traqueur.items.api.services.AttributeApplicatorService;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Paper implementation of {@link AttributeApplicatorService} using the DataComponent API.
 * Registered via {@code META-INF/services/} and discovered by {@link java.util.ServiceLoader}.
 */
public class PaperAttributeApplicatorService implements AttributeApplicatorService {

    @Override
    public void applyAttributes(ItemStack itemStack, List<AttributeWrapper> attributes,
                                ItemsPlugin plugin, AttributeMergeStrategy strategy) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        ItemAttributeModifiers existing = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        List<AttributeEntry> existingEntries = new ArrayList<>();
        if (existing != null) {
            for (var entry : existing.modifiers()) {
                existingEntries.add(new AttributeEntry(entry.attribute(), entry.modifier()));
            }
        }

        List<AttributeEntry> newEntries = new ArrayList<>();
        for (AttributeWrapper wrapper : attributes) {
            AttributeModifier modifier = wrapper.toAttributeModifier(plugin);
            newEntries.add(new AttributeEntry(wrapper.attribute(), modifier));
        }

        List<AttributeEntry> result = merge(existingEntries, newEntries, strategy);
        for (AttributeEntry entry : result) {
            builder.addModifier(entry.attribute(), entry.modifier());
        }

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private List<AttributeEntry> merge(List<AttributeEntry> existing, List<AttributeEntry> incoming,
                                       AttributeMergeStrategy strategy) {
        return switch (strategy) {
            case REPLACE -> new ArrayList<>(incoming);
            case ADD -> {
                List<AttributeEntry> r = new ArrayList<>(existing);
                r.addAll(incoming);
                yield r;
            }
            case KEEP_HIGHEST -> keepByComparator(existing, incoming, Comparator.comparingDouble(e -> e.modifier().getAmount()));
            case KEEP_LOWEST  -> keepByComparator(existing, incoming, Comparator.comparingDouble((AttributeEntry e) -> e.modifier().getAmount()).reversed());
            case SUM -> sumEntries(existing, incoming);
        };
    }

    private List<AttributeEntry> keepByComparator(List<AttributeEntry> existing, List<AttributeEntry> incoming,
                                                   Comparator<AttributeEntry> comparator) {
        List<AttributeEntry> all = new ArrayList<>(existing);
        all.addAll(incoming);
        Map<ModifierKey, List<AttributeEntry>> grouped = all.stream()
                .collect(Collectors.groupingBy(e -> new ModifierKey(e.attribute(), e.modifier().getOperation(), e.modifier().getSlotGroup())));
        List<AttributeEntry> result = new ArrayList<>();
        for (List<AttributeEntry> group : grouped.values()) {
            group.stream().max(comparator).ifPresent(result::add);
        }
        return result;
    }

    private List<AttributeEntry> sumEntries(List<AttributeEntry> existing, List<AttributeEntry> incoming) {
        List<AttributeEntry> all = new ArrayList<>(existing);
        all.addAll(incoming);
        Map<ModifierKey, List<AttributeEntry>> grouped = all.stream()
                .collect(Collectors.groupingBy(e -> new ModifierKey(e.attribute(), e.modifier().getOperation(), e.modifier().getSlotGroup())));
        List<AttributeEntry> result = new ArrayList<>();
        for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
            List<AttributeEntry> entries = group.getValue();
            if (entries.isEmpty()) continue;
            double total = entries.stream().mapToDouble(e -> e.modifier().getAmount()).sum();
            AttributeModifier first = entries.getFirst().modifier();
            result.add(new AttributeEntry(group.getKey().attribute(),
                    new AttributeModifier(first.getKey(), total, first.getOperation(), first.getSlotGroup())));
        }
        return result;
    }

    private record AttributeEntry(Attribute attribute, AttributeModifier modifier) {}
    private record ModifierKey(Attribute attribute, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup) {}
}