package fr.maxlego08.items.enchantments;

import fr.maxlego08.items.api.enchantments.EssentialsEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public record ZEssentialsEnchantment(Enchantment enchantment, List<String> aliases) implements EssentialsEnchantment {

}
