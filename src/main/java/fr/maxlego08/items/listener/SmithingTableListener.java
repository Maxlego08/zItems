package fr.maxlego08.items.listener;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SmithingTableListener implements Listener {

    private final RuneManager runeManager;
    private final ItemManager itemManager;

    public SmithingTableListener(ItemManager itemManager, RuneManager runeManager) {
        this.runeManager = runeManager;
        this.itemManager = itemManager;
    }


    private Optional<Item> getCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return Optional.empty();

        return itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
    }

    private Optional<Rune> getRune(ItemStack item) {
        AtomicReference<Rune> rune = new AtomicReference<>();
        this.getCustomItem(item).ifPresent(runeItem -> {
            if(runeItem.getConfiguration().getItemType() == ItemType.RUNE) {
                rune.set(runeItem.getConfiguration().getItemRuneConfiguration().rune());
            }
        });
        return Optional.ofNullable(rune.get());
    }

    @EventHandler
    public void onSmithing(PrepareSmithingEvent event) {
        var recipe = event.getInventory().getRecipe();
        if (recipe == null) return;
        if(!(recipe instanceof SmithingRecipe smithingRecipe)) {
            return;
        }
        Optional<Rune> runeOptional = this.getRune(event.getInventory().getItem(2));
        if(runeOptional.isEmpty()) {
            return;
        }
        var key = smithingRecipe.getKey();
        for (Map.Entry<Rune, List<ItemRecipe>> runeListEntry : this.runeManager.getRecipesUseRunes().entrySet()) {
            Rune rune = runeListEntry.getKey();
            if (!runeOptional.get().getName().equals(rune.getName())) {
                continue;
            }
            for (ItemRecipe runeRecipe : runeListEntry.getValue()) {
                if(!runeRecipe.getKey().equals(key)) {
                    continue;
                }
                var result = event.getResult();
                if(result == null) {
                    continue;
                }
                try {
                    result = event.getInventory().getItem(1);
                    if(result == null) {
                        continue;
                    }
                    result = result.clone();
                    this.runeManager.applyRune(result, rune);
                    event.setResult(result);
                    return;
                } catch (Exception e) {
                    result = new ItemStack(Material.AIR);
                    event.setResult(result);
                    return;
                }

            }
        }
    }
}
