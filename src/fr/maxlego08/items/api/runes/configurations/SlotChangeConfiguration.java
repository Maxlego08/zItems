package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.handlers.InventorySlotChangeHandler;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class SlotChangeConfiguration extends RuneConfiguration {

    private final InventorySlotChangeHandler.InventorySlotChangeType type;
    private final List<String> commands;

    public SlotChangeConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.type = InventorySlotChangeHandler.InventorySlotChangeType.valueOf(configuration.getString("type"));
        this.commands = configuration.getStringList("commands");
    }

    public List<String> getCommands() {
        return commands;
    }

    public InventorySlotChangeHandler.InventorySlotChangeType getType() {
        return type;
    }
}
