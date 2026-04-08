package fr.traqueur.items.listeners;

import fr.traqueur.items.api.interactions.InteractionAction;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.items.metadata.CommandsMetadata;
import fr.traqueur.items.items.metadata.CommandsMetadata.ItemCommand;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.api.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener that handles command execution from CommandsMetadata.
 */
public class CommandsListener implements Listener {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType().isAir()) {
            return;
        }

        // Get the item ID from persistent data
        String itemId = Keys.ITEM_ID.get(itemStack.getItemMeta().getPersistentDataContainer()).orElse(null);
        if (itemId == null) {
            return;
        }

        // Get the Item from the registry
        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        Item item = registry.getById(itemId);
        if (item == null) {
            return;
        }

        // Check if the item has CommandsMetadata
        if (item.settings().metadata() == null) {
            return;
        }

        CommandsMetadata commandsMetadata = item.settings().metadata().stream()
                .filter(m -> m instanceof CommandsMetadata)
                .map(m -> (CommandsMetadata) m)
                .findFirst()
                .orElse(null);

        if (commandsMetadata == null) {
            return;
        }

        // Get the interaction action
        InteractionAction action = getActionFromEvent(event, player);
        if (action == null) {
            return;
        }

        // Execute commands
        executeCommands(player, itemStack, commandsMetadata, action, event);
    }

    private void executeCommands(Player player, ItemStack itemStack, CommandsMetadata metadata, InteractionAction action, PlayerInteractEvent event) {
        for (ItemCommand command : metadata.commands()) {
            // Check action match
            if (command.action() != null && command.action() != InteractionAction.CLICK && command.action() != action) {
                continue;
            }

            // Check cooldown
            if (command.cooldown() > 0) {
                if (isOnCooldown(player, itemStack, command)) {
                    continue;
                }
                updateCooldown(player, itemStack, command);
            }

            // Execute commands
            if (command.commands() != null && !command.commands().isEmpty()) {
                for (String cmd : command.commands()) {
                    String processedCmd = cmd.replace("%player%", player.getName());

                    switch (command.sender()) {
                        case PLAYER -> Bukkit.dispatchCommand(player, processedCmd);
                        case CONSOLE -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCmd);
                    }
                }
            }

            // Send messages
            if (command.messages() != null && !command.messages().isEmpty()) {
                for (String message : command.messages()) {
                    MessageUtil.sendMessage(player, message);
                }
            }

            // Apply damage
            if (command.damage() != null) {
                applyDamage(player, itemStack, command);
            }
        }
    }

    private void applyDamage(Player player, ItemStack itemStack, ItemCommand command) {
        if (command.damage() == null) {
            return;
        }

        switch (command.damage().type()) {
            case DURABILITY -> JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(itemStack, command.damage().quantity(), player);
            case AMOUNT -> {
                int newAmount = itemStack.getAmount() - command.damage().quantity();
                if (newAmount <= 0) {
                    player.getInventory().remove(itemStack);
                } else {
                    itemStack.setAmount(newAmount);
                }
            }
        }
    }

    private boolean isOnCooldown(Player player, ItemStack itemStack, ItemCommand command) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return false;
        }

        String key = getCooldownKey(itemStack, command);
        Long lastUse = playerCooldowns.get(key);
        if (lastUse == null) {
            return false;
        }

        return System.currentTimeMillis() - lastUse < command.cooldown();
    }

    private void updateCooldown(Player player, ItemStack itemStack, ItemCommand command) {
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        String key = getCooldownKey(itemStack, command);
        playerCooldowns.put(key, System.currentTimeMillis());
    }

    private String getCooldownKey(ItemStack itemStack, ItemCommand command) {
        String itemId = Keys.ITEM_ID.get(itemStack.getItemMeta().getPersistentDataContainer()).orElse("unknown");
        return itemId + "_" + command.hashCode();
    }

    private InteractionAction getActionFromEvent(PlayerInteractEvent event, Player player) {
        return switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR ->
                    player.isSneaking() ? InteractionAction.SHIFT_RIGHT_CLICK : InteractionAction.RIGHT_CLICK;
            case LEFT_CLICK_BLOCK, LEFT_CLICK_AIR ->
                    player.isSneaking() ? InteractionAction.SHIFT_LEFT_CLICK : InteractionAction.LEFT_CLICK;
            default -> null;
        };
    }
}
