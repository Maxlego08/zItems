package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RuneDataType implements PersistentDataType<byte[], List<Rune>> {

    private final RuneManager runeManager;

    public RuneDataType(RuneManager runeManager) {
        this.runeManager = runeManager;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<List<Rune>> getComplexType() {
        return (Class<List<Rune>>) (Class<?>) List.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(List<Rune> complex, @NotNull PersistentDataAdapterContext context) {
        List<String> serializedRunes = new ArrayList<>();
        for (Rune rune : complex) {
            serializedRunes.add(rune.getName());
        }
        String joinedData = String.join(";", serializedRunes);
        return joinedData.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull List<Rune> fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        String data = new String(primitive, StandardCharsets.UTF_8);
        String[] serializedRunes = data.split(";");
        List<Rune> runes = new ArrayList<>();
        for (String serializedRune : serializedRunes) {
            runeManager.getRune(serializedRune).ifPresent(runes::add);
        }
        return runes;
    }
}
