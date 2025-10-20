package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.runes.RuneActivator;

public class Empty implements RuneActivator {
    @Override
    public int getPriority() {
        return -1;
    }
}
