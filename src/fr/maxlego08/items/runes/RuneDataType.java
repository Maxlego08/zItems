package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class RuneDataType implements PersistentDataType<String, Rune> {

    private final RuneManager runeManager;

    public RuneDataType(RuneManager runeManager) {
        this.runeManager = runeManager;
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Rune> getComplexType() {
        return Rune.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Rune rune, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return rune.getName();
    }

    @Override
    public @NotNull Rune fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return runeManager.getRune(s).orElseThrow();
    }
}
