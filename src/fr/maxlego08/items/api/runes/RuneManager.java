package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.runes.RuneTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface RuneManager {

    void loadRunes();

    void loadRune(File file);

    Optional<Rune> getRune(String name);

    List<Rune> getRunes();

    List<Rune> getRunes(RuneTypes runeType);

    void applyRune(Player player, String runName);

    NamespacedKey getKey();

    PersistentDataType<byte[], List<Rune>> getDataType();
}
