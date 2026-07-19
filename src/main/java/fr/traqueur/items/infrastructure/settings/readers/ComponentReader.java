package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import net.kyori.adventure.text.Component;

public class ComponentReader implements Reader<Component> {
    @Override
    public Component read(String s) throws StructuraException {
        return MessageUtil.parseMessage(s);
    }
}
