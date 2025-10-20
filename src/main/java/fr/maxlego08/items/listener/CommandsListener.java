package fr.maxlego08.items.listener;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.commands.CommandSender;
import fr.maxlego08.items.api.configurations.commands.CommandsConfiguration;
import fr.maxlego08.items.api.configurations.commands.ItemCommand;
import fr.maxlego08.items.zcore.utils.ZUtils;
import fr.maxlego08.items.zcore.utils.builder.CooldownBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandsListener extends ZUtils implements Listener {

    private final ItemsPlugin plugin;
    private final ItemComponent itemComponent;
    private final ItemManager itemManager;

    public CommandsListener(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.itemComponent = plugin.getItemComponent();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        var player = event.getPlayer();
        org.bukkit.event.block.Action action = event.getAction();

        confirm(player, action, true);
    }

    private String generateCooldownName(Item item, ItemCommand itemCommand) {
        return "command-" + item.getName() + "-" + itemCommand.action().name() + "-" + String.join("-", itemCommand.commands()).trim();
    }

    public void confirm(Player player, Action action, boolean checkConfirmation) {

        var itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;

        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        Item item = itemOptional.get();
        var commandConfiguration = item.getConfiguration().getCommandsConfiguration();

        if (commandConfiguration.needConfirmation() && checkConfirmation) {
            var manager = this.plugin.getInventoryManager();
            manager.openInventory(player, plugin, "item_confirmation");
            return;
        }

        useItem(player, commandConfiguration, action, item, itemStack, itemMeta);

    }

    private void useItem(Player player, CommandsConfiguration commandConfiguration, Action action, Item item, ItemStack itemStack, ItemMeta itemMeta) {
        List<ItemCommand> commands = commandConfiguration.commands();
        fr.maxlego08.items.api.configurations.commands.Action itemAction;

        if (action.isLeftClick()) {
            if (player.isSneaking()) {
                itemAction = fr.maxlego08.items.api.configurations.commands.Action.SHIFT_LEFT_CLICK;
            } else {
                itemAction = fr.maxlego08.items.api.configurations.commands.Action.LEFT_CLICK;
            }
        } else if (action.isRightClick()) {
            if (player.isSneaking()) {
                itemAction = fr.maxlego08.items.api.configurations.commands.Action.SHIFT_RIGHT_CLICK;
            } else {
                itemAction = fr.maxlego08.items.api.configurations.commands.Action.RIGHT_CLICK;
            }
        } else {
            return;
        }

        for (ItemCommand itemCommand : commands.stream().filter(command -> command.action() == itemAction || command.action() == fr.maxlego08.items.api.configurations.commands.Action.CLICK).collect(Collectors.toSet())) {

            if (itemCommand.cooldown() > 0 && CooldownBuilder.isCooldown(this.generateCooldownName(item, itemCommand), player.getUniqueId())) {
                return;
            }

            for (String command : itemCommand.commands()) {
                String commandStr = command.replace("%player%", player.getName());
                if (itemCommand.sender() == CommandSender.PLAYER) {
                    player.performCommand(commandStr);
                } else {
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandStr);
                }
            }

            for (String message : itemCommand.messages()) {
                this.itemComponent.sendMessage(player, papi(message, player));
            }

            if (itemCommand.cooldown() > 0) {
                CooldownBuilder.addCooldown(this.generateCooldownName(item, itemCommand), player.getUniqueId(), itemCommand.cooldown());
            }

            if (itemCommand.damage() == null) {
                return;
            }

            ItemCommand.ItemDamage damage = itemCommand.damage();
            if (damage.type() == ItemCommand.DamageType.AMOUNT) {
                itemStack.setAmount(Math.max(itemStack.getAmount() - damage.damage(), 0));
            } else {
                if (!(itemMeta instanceof Damageable damageable)) return;
                if (damageable.getDamage() + damage.damage() >= damageable.getMaxDamage()) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    damageable.setDamage(damageable.getDamage() + damage.damage());
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
    }

}
