package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.SlotChangeConfiguration;
import fr.maxlego08.items.api.runes.handlers.InventorySlotChangeHandler;
import fr.maxlego08.items.zcore.utils.ZUtils;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;

public class SlotChange extends ZUtils implements RuneActivator, InventorySlotChangeHandler<SlotChangeConfiguration> {
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public InventorySlotChangeType getType(SlotChangeConfiguration configuration) {
        return configuration.getType();
    }

    @Override
    public void onInventorySlotChange(ItemPlugin plugin, Player player, SlotChangeConfiguration runeConfiguration) {
        runeConfiguration.getCommands().forEach(command -> {
            String commandFormatted = papi(command.replace("%player%", player.getName()), player);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandFormatted);
        });
    }
}
