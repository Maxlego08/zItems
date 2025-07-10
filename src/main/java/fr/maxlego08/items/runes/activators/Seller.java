package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneSellingConfiguration;
import fr.maxlego08.items.api.runes.handlers.BreakHandler;
import fr.maxlego08.items.api.runes.handlers.EntityDeathHandler;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Seller implements RuneActivator, BreakHandler<RuneSellingConfiguration>, EntityDeathHandler<RuneSellingConfiguration> {
    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneSellingConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        Plugins plugins = runeConfiguration.getPlugins();
        ShopProvider provider = plugins == null ? plugin.getHookManager().getProviders().values().stream().findFirst().orElse(null) : plugin.getHookManager().getProviders().get(plugins);
        if(provider == null) return origin;

        for (Map.Entry<Location, List<ItemStack>> locationListEntry : drops.entrySet()) {
            List<ItemStack> itemStacks = locationListEntry.getValue();
            List<ItemStack> dropsList = new ArrayList<>(itemStacks);

            dropsList.forEach(itemStack -> {
                boolean result = provider.sellItems(event.getPlayer(), itemStack, itemStack.getAmount(), runeConfiguration.getMultiplier());
                if (result) itemStacks.remove(itemStack);
            });
            drops.put(locationListEntry.getKey(), itemStacks);
        }
        drops.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return origin;
    }

    @Override
    public void onEntityDeath(ItemPlugin plugin, EntityDeathEvent event, RuneSellingConfiguration runeConfiguration) {
        Plugins plugins = runeConfiguration.getPlugins();
        ShopProvider provider = plugins == null ? plugin.getHookManager().getProviders().values().stream().findFirst().orElse(null) : plugin.getHookManager().getProviders().get(plugins);
        if(provider == null) return;

        List<ItemStack> drops = new ArrayList<>(event.getDrops());

        drops.forEach(itemStack -> {
            boolean result = provider.sellItems(event.getEntity().getKiller(), itemStack, itemStack.getAmount(), runeConfiguration.getMultiplier());
            if (result) event.getDrops().remove(itemStack);
        });
    }
}
