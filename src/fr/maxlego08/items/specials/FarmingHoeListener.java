package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.configurations.specials.FarmingHoeConfiguration;
import fr.maxlego08.items.api.events.FarmingHoeBlockBreakEvent;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

public class FarmingHoeListener implements Listener {

    private final ItemsPlugin plugin;

    public FarmingHoeListener(ItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof FarmingHoeBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();

        if (!itemStack.hasItemMeta()) return;

        var world = player.getWorld();
        var itemMeta = itemStack.getItemMeta();
        var persistentDataContainer = itemMeta.getPersistentDataContainer();
        var farmBlock = event.getBlock();

        if (!persistentDataContainer.has(FarmingHoeConfiguration.FARMING_HOE_SIZE_KEY, PersistentDataType.INTEGER))
            return;

        int size = persistentDataContainer.get(FarmingHoeConfiguration.FARMING_HOE_SIZE_KEY, PersistentDataType.INTEGER);
        int range = size / 2;

        event.setCancelled(true);
        itemStack.damage(1, player);

        for (int x = -range; x < range + 1; x++) {
            for (int z = -range; z < range + 1; z++) {

                var block = world.getBlockAt(farmBlock.getX() + x, farmBlock.getY(), farmBlock.getZ() + z);

                if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {

                    var blockEvent = new FarmingHoeBlockBreakEvent(block, player);
                    blockEvent.callEvent();

                    if (blockEvent.isCancelled()) continue;

                    var drops = block.getDrops(itemStack, player);
                    drops.forEach(itemStackDrop -> world.dropItemNaturally(farmBlock.getLocation(), itemStackDrop));

                    ageable.setAge(0);
                    block.setBlockData(ageable);
                    block.getState().update();
                }
            }
        }

    }
}
