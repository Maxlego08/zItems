package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.settings.models.EnchantmentWrapper;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoMetadata("enchant-storage")
public record EnchantStorageMetadata(List<EnchantmentWrapper> enchantments) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        boolean applied = ItemUtil.editMeta(itemStack, EnchantmentStorageMeta.class, meta -> {
            for (EnchantmentWrapper enchantment : enchantments) {
                int level = enchantment.level();
                if (level < 1) {
                    meta.removeStoredEnchant(enchantment.enchantment());
                    continue;
                }
                meta.addStoredEnchant(enchantment.enchantment(), enchantment.level(), true);
            }
        });
        if (!applied) {
            throw new IllegalStateException("Failed to apply EnchantmentStorageMeta to ItemStack of type " + itemStack.getType().name());
        }
    }
}
