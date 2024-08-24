package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.runes.exceptions.RuneException;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface RuneManager {

    void loadRunes();

    void loadRune(File file);

    Optional<Rune> getRune(String name);

    List<Rune> getRunes();

    List<Rune> getRunes(RuneType runeType);

    void applyRune(Player player, String runName);

    void applyRune(ItemStack itemStack, Rune rune) throws RuneException;

    NamespacedKey getKey();

    PersistentDataType<String, Rune> getDataType();
}
