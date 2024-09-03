package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.enchantments.EssentialsEnchantment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuneEnchantApplicatorConfiguration extends RuneConfiguration {

    public record EnchantmentEvolution(Enchantment enchantment, int evolution) {}

    private final List<EnchantmentEvolution> enchantmentEvolutions;

    public RuneEnchantApplicatorConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.enchantmentEvolutions = stringListToEnchantmentEvolutionList(plugin, configuration.getList("enchantments"));
    }

    private List<EnchantmentEvolution> stringListToEnchantmentEvolutionList(ItemPlugin plugin, List<?> enchantments) {
        return  enchantments.stream().map(enchantment -> {
            Map<String, Object> mapEnchantment = (Map<String, Object>) enchantment;
            EssentialsEnchantment enchant = plugin.getEnchantments().getEnchantments((String) mapEnchantment.get("name")).orElseThrow(() -> new IllegalArgumentException("Enchantement not found"));
            if (mapEnchantment.containsKey("increase")) {
                return new EnchantmentEvolution(enchant.enchantment(), (int) mapEnchantment.get("increase"));
            } else if (mapEnchantment.containsKey("decrease")) {
                return new EnchantmentEvolution(enchant.enchantment(), -(int) mapEnchantment.get("decrease"));
            } else {
                throw new IllegalArgumentException("Increase or decrease not found");
            }
        }).collect(Collectors.toList());
    }

    public List<EnchantmentEvolution> getEnchantmentEvolutions() {
        return enchantmentEvolutions;
    }
}
