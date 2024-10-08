package fr.maxlego08.items.api.configurations.commands;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.zcore.utils.builder.CooldownBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        org.bukkit.event.block.Action action = event.getAction();
        Item item = itemOptional.get();
        List<ItemCommand> commands = item.getConfiguration().getCommandsConfiguration().commands();
        Action itemAction;

        if (action.isLeftClick()) {
            itemAction = Action.LEFT_CLICK;
        } else if (action.isRightClick()) {
            itemAction = Action.RIGHT_CLICK;
        } else {
            return;
        }

        for (ItemCommand itemCommand : commands.stream().filter(command -> command.action() == itemAction || command.action() == Action.CLICK).collect(Collectors.toSet())) {
            String commandStr = itemCommand.command().replace("%player%", player.getName());

            if (itemCommand.cooldown() > 0 && CooldownBuilder.isCooldown(this.generateCooldownName(item, itemCommand), player.getUniqueId())) {
                //TODO add message
                return;
            }

            if(itemCommand.sender() == CommandSender.PLAYER) {
                player.performCommand(commandStr);
            } else {
                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandStr);
            }

            if(itemCommand.cooldown() > 0) {
                CooldownBuilder.addCooldown(this.generateCooldownName(item, itemCommand), player.getUniqueId(), itemCommand.cooldown());
            }


            if(itemCommand.damage() == null) {
                return;
            }

            ItemCommand.ItemDamage damage = itemCommand.damage();
            if(damage.type() == ItemCommand.DamageType.AMOUNT) {
                itemStack.setAmount(Math.max(itemStack.getAmount() - damage.damage(), 0));
            } else {
                if(!(meta instanceof Damageable damageable)) return;
                if(damageable.getDamage() + damage.damage() >= damageable.getMaxDamage()) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    damageable.setDamage(damageable.getDamage() + damage.damage());
                    itemStack.setItemMeta(meta);
                }
            }

        }


    }

    private String generateCooldownName(Item item, ItemCommand itemCommand) {
        return "command-" + item.getName() + "-" + itemCommand.action().name() + "-" + itemCommand.command().trim();
    }

}
