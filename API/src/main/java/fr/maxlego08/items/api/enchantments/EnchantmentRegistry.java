package fr.maxlego08.items.api.enchantments;

import org.bukkit.enchantments.Enchantment;

import java.util.List;

public interface EnchantmentRegistry {

    Enchantment enchantment();

    List<String> aliases();

}
