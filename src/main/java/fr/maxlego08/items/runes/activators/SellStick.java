package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.commands.Action;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneSellingConfiguration;
import fr.maxlego08.items.api.runes.handlers.BreakHandler;
import fr.maxlego08.items.api.runes.handlers.EntityDeathHandler;
import fr.maxlego08.items.api.runes.handlers.InteractionHandler;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SellStick implements RuneActivator, InteractionHandler<RuneSellingConfiguration> {
    @Override
    public int getPriority() {
        return 0;
    }


    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent event, RuneSellingConfiguration runeConfiguration) {
        if(event.getHand() != runeConfiguration.getHand()) {
            return;
        }
        Plugins plugins = runeConfiguration.getPlugins();
        ShopProvider provider = plugins == null ? plugin.getHookManager().getProviders().values().stream().findFirst().orElse(null) : plugin.getHookManager().getProviders().get(plugins);
        Player player = event.getPlayer();
        if(provider == null) return;

        if(event.getItem() == null) return;

        Block block = event.getClickedBlock();
        if(block == null) return;

        Action action;
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if(player.isSneaking()) {
                    action = Action.SHIFT_RIGHT_CLICK;
                } else {
                    action = Action.RIGHT_CLICK;
                }
                break;
            case LEFT_CLICK_BLOCK:
                if(player.isSneaking()) {
                    action = Action.SHIFT_LEFT_CLICK;
                } else {
                    action = Action.LEFT_CLICK;
                }
                break;
            default:
                return;
        }

        if(runeConfiguration.getAction() != Action.CLICK && runeConfiguration.getAction() != action) {
            return;
        }

        if (!(block.getState() instanceof Container container)) {
            return;
        }

        List<ItemStack> itemStacks = new ArrayList<>();
        for (ItemStack itemStack : container.getInventory().getContents()) {
            if(itemStack == null) {
                itemStacks.add(new ItemStack(Material.AIR));
            } else {
                boolean result = provider.sellItems(event.getPlayer(), itemStack, itemStack.getAmount(), runeConfiguration.getMultiplier());
                if(!result) {
                    itemStacks.add(itemStack);
                }
            }
        }
        container.getInventory().setContents(itemStacks.toArray(new ItemStack[0]));
        event.setCancelled(true);
        if(runeConfiguration.isDamage()) {
            PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(event.getPlayer(), event.getItem(), 1, 1);
            plugin.getServer().getPluginManager().callEvent(damageEvent);
            if(damageEvent.isCancelled()) {
                return;
            }
            if(event.getItem().getItemMeta() instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + damageEvent.getDamage());
                event.getItem().setItemMeta(damageable);
            } else {
                int amount = event.getItem().getAmount();
                if(amount > damageEvent.getDamage()) {
                    event.getItem().setAmount(amount - damageEvent.getDamage());
                } else {
                    event.getPlayer().getInventory().remove(event.getItem());
                }
            }

        }
    }
}
