package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@AutoMetadata("can-break")
@PaperOnly
public record CanBreakMetadata(List<Material> blocks) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        List<BlockType> blockTypes = blocks.stream().map(Material::asBlockType).filter(Objects::nonNull).toList();
        RegistryKeySet<@NotNull BlockType> registryKeySet = RegistrySet.keySetFromValues(RegistryKey.BLOCK, blockTypes);
        BlockPredicate blockPredicate = BlockPredicate.predicate()
                .blocks(registryKeySet)
                .build();

        ItemAdventurePredicate canPlaceOnPredicate = ItemAdventurePredicate.itemAdventurePredicate(List.of(blockPredicate));
        itemStack.setData(DataComponentTypes.CAN_BREAK, canPlaceOnPredicate);
    }
}