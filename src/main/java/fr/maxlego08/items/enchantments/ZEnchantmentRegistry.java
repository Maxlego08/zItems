package fr.maxlego08.items.enchantments;

import fr.maxlego08.items.api.enchantments.EnchantmentRegistry;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public record ZEnchantmentRegistry(Enchantment enchantment, List<String> aliases) implements EnchantmentRegistry {

}
