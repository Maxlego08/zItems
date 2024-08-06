package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.specials.FarmingHoeConfiguration;
import fr.maxlego08.items.api.events.FarmingHoeBlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class FarmingHoeListener extends SpecialHelper<FarmingHoeConfiguration> implements Listener {

    public FarmingHoeListener(ItemsPlugin plugin) {
        super(plugin, ItemType.FARMING_HOE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof FarmingHoeBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();

        if (!isSpecialItem(itemStack)) return;

        var world = player.getWorld();
        var itemMeta = itemStack.getItemMeta();
        var farmBlock = event.getBlock();

        if (!(farmBlock.getBlockData() instanceof Ageable)) return;

        FarmingHoeConfiguration farmingHoeConfiguration = getSpecialConfiguration(itemStack);
        int range = farmingHoeConfiguration.size() / 2;

        event.setCancelled(true);
        boolean needToRemoveDamage = false;

        for (int x = -range; x < range + 1; x++) {
            for (int z = -range; z < range + 1; z++) {

                var block = world.getBlockAt(farmBlock.getX() + x, farmBlock.getY(), farmBlock.getZ() + z);

                if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {

                    var blockEvent = new FarmingHoeBlockBreakEvent(block, player);
                    blockEvent.callEvent();

                    if (blockEvent.isCancelled()) continue;

                    needToRemoveDamage = true;
                    var drops = block.getDrops(itemStack, player);

                    var dropLocation = switch (farmingHoeConfiguration.dropItemType()) {
                        case BLOCK -> block.getLocation();
                        case CENTER -> farmBlock.getLocation();
                        case PLAYER -> player.getLocation();
                    };

                    dropItem(player, drops, dropLocation, farmingHoeConfiguration);

                    if (farmingHoeConfiguration.autoPlant()) {
                        ageable.setAge(0);
                        block.setBlockData(ageable);
                        block.getState().update();
                    } else {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        if (needToRemoveDamage) itemStack.damage(1, player);
    }

    private void dropItem(Player player, Collection<ItemStack> drops, Location location, FarmingHoeConfiguration farmingHoeConfiguration) {
        World world = location.getWorld();

        if (farmingHoeConfiguration.dropItemInInventory()) {

            var inventory = player.getInventory();

            var result = inventory.addItem(drops.toArray(new ItemStack[0]));
            if (result.isEmpty()) return;

            System.out.println(result);
            result.values().forEach(itemStackDrop -> world.dropItemNaturally(location, itemStackDrop));

        } else drops.forEach(itemStackDrop -> world.dropItemNaturally(location, itemStackDrop));
    }
}
