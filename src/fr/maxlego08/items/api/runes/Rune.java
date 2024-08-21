package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public interface Rune {

    String getName();

    String getDisplayName();

    RuneType getType();

    List<Material> getMaterials();

    List<Tag<Material>> getTags();

    boolean isAllowed(Material material);

    <T extends RuneConfiguration> T getConfiguration();

}
