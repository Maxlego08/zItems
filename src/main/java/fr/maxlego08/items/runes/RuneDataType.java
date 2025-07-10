package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class RuneDataType implements PersistentDataType<String, Rune> {

    private final RuneManager runeManager;

    public RuneDataType(RuneManager runeManager) {
        this.runeManager = runeManager;
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public  Class<Rune> getComplexType() {
        return Rune.class;
    }

    @Override
    public  String toPrimitive( Rune rune,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return rune.getName();
    }

    @Override
    public  Rune fromPrimitive( String s,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return runeManager.getRune(s).orElseThrow();
    }
}
