package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoMetadata("custom-model")
@PaperOnly
public record CustomModelMetadata(
        @Options(optional = true) List<Color> colors,
        @Options(optional = true) List<Double> floats,
        @Options(optional = true) List<String> strings
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        CustomModelData.Builder builder = CustomModelData.customModelData();

        if (colors != null && !colors.isEmpty()) {
            builder.addColors(colors);
        }
        if (floats != null && !floats.isEmpty()) {
            builder.addFloats(floats.stream().map(Double::floatValue).toList());
        }
        if (strings != null && !strings.isEmpty()) {
            builder.addStrings(strings);
        }

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
    }
}