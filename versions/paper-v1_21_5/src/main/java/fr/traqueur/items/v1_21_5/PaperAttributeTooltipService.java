package fr.traqueur.items.v1_21_5;

import fr.traqueur.items.api.services.AttributeTooltipService;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.inventory.ItemStack;

/**
 * Paper 1.21.5+ implementation of {@link AttributeTooltipService}.
 * Uses the {@code TOOLTIP_DISPLAY} data component API to hide the attribute modifiers section.
 * <p>
 * Registered via {@code META-INF/services/} and discovered at runtime by {@link java.util.ServiceLoader}.
 * Only loaded on Paper 1.21.5+ (the data component API is absent on older versions and Spigot).
 */
public class PaperAttributeTooltipService implements AttributeTooltipService {

    @Override
    public void hideAttributesTooltip(ItemStack itemStack) {
        // On Paper 1.21.5+, ItemFlag.HIDE_ATTRIBUTES is deprecated when ATTRIBUTE_MODIFIERS
        // was written via setData(), so we use the TOOLTIP_DISPLAY data component instead.
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
        TooltipDisplay existing = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        if (existing != null) {
            builder.hideTooltip(existing.hideTooltip());
            if (!existing.hiddenComponents().isEmpty()) {
                builder.hiddenComponents(existing.hiddenComponents());
            }
        }
        builder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
    }
}
