package fr.traqueur.items.blocks;

import fr.traqueur.items.api.blocks.CustomBlockProvider;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ZItemsProvider implements CustomBlockProvider {

    @Override
    public Optional<List<ItemStack>> getCustomBlockDrop(Block block, Player player) {
        Optional<ItemStack> customDrop = BlockTracker.get().getCustomBlockDrop(block, player);
        if (customDrop.isPresent()) {
            BlockTracker.get().untrackBlock(block);
        }
        return customDrop.map(List::of);
    }

    @Override
    public Optional<String> getCustomBlockId(Block block) {
        return BlockTracker.get().getTrackedItemId(block);
    }

    @Override
    public void placeCustomBlock(String itemId, Block block) {
        ItemsRegistry itemsRegistry = Registry.get(ItemsRegistry.class);
        Item item = itemsRegistry.getById(itemId);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        BlockTracker.get().trackBlock(block, itemId);
        Material material = item.material();
        block.setType(material);
    }

}
