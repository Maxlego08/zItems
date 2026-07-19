package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.inventory.EquipmentSlotGroup;

public class EquipmentSlotGroupReader implements Reader<EquipmentSlotGroup> {
    @Override
    public EquipmentSlotGroup read(String s) throws StructuraException {
        EquipmentSlotGroup group = EquipmentSlotGroup.getByName(s);
        if (group == null) {
            throw new StructuraException("Unknown EquipmentSlotGroup: " + s);
        }
        return group;
    }
}
