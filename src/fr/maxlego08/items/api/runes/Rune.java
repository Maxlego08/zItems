package fr.maxlego08.items.api.runes;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public interface Rune {

    String getName();

    String getDisplayName();

    RuneType getType();

    List<Material> getMaterials();

    List<Tag<Material>> getTags();

}
