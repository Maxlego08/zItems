package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.configurations.recipes.ItemRecipe;
import fr.maxlego08.items.api.runes.exceptions.RuneException;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RuneManager {

    void loadRunes();

    void loadCraftWithRunes();

    void loadRune(File file);

    Optional<Rune> getRune(String name);

    List<Rune> getRunes();

    List<Rune> getRunes(RuneType runeType);

    Optional<List<Rune>> getRunes(ItemStack itemStack);

    void applyRune(Player player, String runName);

    void applyRune(ItemStack itemStack, Rune rune) throws RuneException;

    NamespacedKey getKey();

    NamespacedKey getRuneRepresentKey();

    PersistentDataType<String, Rune> getDataType();

    void deleteCrafts();

    Map<NamespacedKey, ItemRecipe> getRecipesUseRunes();

    <T extends PlayerEvent> void onPlayerEvent(T event);
}
