package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.runes.RuneTypes;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface RuneType {

    List<RuneType> runeTypes = new ArrayList<>(Arrays.asList(RuneTypes.values()));

    static void register(RuneType runeType) {
        runeTypes.add(runeType);
    }

    static List<RuneType> getRuneTypes() {
        return runeTypes;
    }

    static Optional<RuneType> getRuneType(String name) {
        return runeTypes.stream().filter(runeType -> runeType.getName().equalsIgnoreCase(name)).findFirst();
    }

    List<RuneType> getIncompatibles();

    String getName();

    RuneActivator getActivator();

    RuneConfiguration getConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName);

}
