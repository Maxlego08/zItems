package fr.maxlego08.items.zcore.utils;

import fr.maxlego08.items.placeholder.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PapiUtils {

    /**
     * Applies PlaceholderAPI transformations to a string.
     *
     * @param placeHolder the string to transform.
     * @param player      the player context for the placeholders.
     * @return the transformed string.
     */
    public String papi(String placeHolder, Player player) {
        return Placeholder.getPlaceholder().setPlaceholders(player, placeHolder);
    }

    /**
     * Applies PlaceholderAPI transformations to a list of strings.
     *
     * @param placeHolder the list of strings to transform.
     * @param player      the player context for the placeholders.
     * @return the transformed list of strings.
     */
    public List<String> papi(List<String> placeHolder, Player player) {
        return Placeholder.getPlaceholder().setPlaceholders(player, placeHolder);
    }
}
