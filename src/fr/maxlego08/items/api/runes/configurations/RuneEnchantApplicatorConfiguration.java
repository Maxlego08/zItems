package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;

public class RuneEnchantApplicatorConfiguration extends RuneConfiguration {

    record EnchantmentEvolution(String name, int evolution) {}

    public RuneEnchantApplicatorConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        List<EnchantmentEvolution> enchantmentEvolutionsList = stringListToEnchantmentEvolutionList(configuration.getList("enchantements"));

        /*
        * enchantments:
          - name: PROTECTION
            augment: 2
        * */
    }

    private List<EnchantmentEvolution> stringListToEnchantmentEvolutionList(ItemPlugin plugin, List<?> enchantements) {
        for (Object enchantement : enchantements) {
            if (!(enchantement instanceof Map)) {
                throw new IllegalArgumentException("Enchantement must be a map");
            }
            Map<String, Object> mapEnchantement = (Map<String, Object>) enchantement;
            String name = mapEnchantement.get("name");
        }
    }
}
