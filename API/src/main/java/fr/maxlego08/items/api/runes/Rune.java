package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public interface Rune {

    /**
     * Get the parent of the rune.
     *
     * @return the parent of the rune
     */
    String getParent();

    /**
     * Get the name of the rune.
     *
     * @return the name of the rune
     */
    String getName();

    /**
     * Get the display name of the rune.
     *
     * @return the display name of the rune
     */
    String getDisplayName();

    /**
     * Get the type of the rune.
     *
     * @return the type of the rune
     */
    RuneType getType();

    /**
     * Get the list of materials associated with the rune.
     *
     * @return the list of materials associated with the rune
     */
    List<Material> getMaterials();

    /**
     * Get the list of tags associated with the rune.
     * A tag is an object that contains a list of materials associated with the rune.
     *
     * @return the list of tags associated with the rune
     */
    List<Tag<Material>> getTags();

    /**
     * Check if a material is allowed by the rune.
     *
     * @param material the material to check
     * @return true if the material is allowed, false otherwise
     */
    boolean isAllowed(Material material);

    /**
     * Gets the configuration of the rune.
     *
     * @return the configuration of the rune
     */
    <T extends RuneConfiguration> T getConfiguration();

}
