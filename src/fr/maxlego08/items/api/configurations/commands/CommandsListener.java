package fr.maxlego08.items.api.configurations.commands;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

public class CommandsListener implements Listener {

    private final ItemManager itemManager;

    public CommandsListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;
        Action action = event.getAction();
        Item item = itemOptional.get();
        List<CommandsConfiguration.ItemCommand> commands = item.getConfiguration().getCommandsConfiguration().commands();
        CommandsConfiguration.Action itemAction;

        if (action.isLeftClick()) {
            itemAction = CommandsConfiguration.Action.LEFT_CLICK;
        } else if (action.isRightClick()) {
            itemAction = CommandsConfiguration.Action.RIGHT_CLICK;
        } else {
            return;
        }

        commands.stream().filter(command -> command.action() == itemAction).forEach(command -> {
            String commandStr = command.command().replace("%player%", player.getName());

            if(command.sender() == CommandsConfiguration.CommandSender.PLAYER) {
                player.performCommand(commandStr);
            } else {
                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandStr);
            }
        });


    }

}
